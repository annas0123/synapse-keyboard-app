package com.smafty.synapsekeyboard.data.local.repository

import android.content.Context
import android.util.Log
import com.smafty.synapsekeyboard.auth.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val TAG = "EnergyQuotaRepository"

/**
 * EnergyQuotaRepository — bridges Supabase `energy_quotas` with the in-memory reactive state.
 *
 * Implements token-accurate tracking of the user's remaining "AI Energy" allowance.
 * Bypasses local Room database completely for real-time consistency.
 *
 * Operations:
 *  - [syncEnergyFromRemote] — called on app startup / HomeScreen resume.
 *  - [consumeEnergy] — called immediately after a successful AI generation.
 *    Also syncs usage to `prompt_usage` table in Supabase.
 */
object EnergyQuotaRepository {

    private val supabase = SupabaseClientProvider.client

    // ─── Shared In-Memory Reactive State flows ────────────────────────────────
    private val _energyRemaining = MutableStateFlow(20_000)
    val energyRemaining: StateFlow<Int> = _energyRemaining.asStateFlow()

    private val _energyUsed = MutableStateFlow(0)
    val energyUsed: StateFlow<Int> = _energyUsed.asStateFlow()

    private val _energyAllowed = MutableStateFlow(20_000)
    val energyAllowed: StateFlow<Int> = _energyAllowed.asStateFlow()

    private val _promptsToday = MutableStateFlow(0)
    val promptsToday: StateFlow<Int> = _promptsToday.asStateFlow()

    private val _energyToday = MutableStateFlow(0)
    val energyToday: StateFlow<Int> = _energyToday.asStateFlow()

    /** Lifetime total of all AI prompts ever run by this user. */
    private val _totalPrompts = MutableStateFlow(0)
    val totalPrompts: StateFlow<Int> = _totalPrompts.asStateFlow()

    // ─── Remote data models ───────────────────────────────────────────────────

    @Serializable
    data class RemoteEnergyQuota(
        val id: String? = null,
        @SerialName("user_id")          val userId:          String,
        @SerialName("energy_remaining") val energyRemaining: Long,
        @SerialName("energy_used")      val energyUsed:      Long,
        @SerialName("total_prompts")    val totalPrompts:    Long = 0
    )

    @Serializable
    data class RemoteEnergyTransaction(
        val id: Long? = null,
        @SerialName("user_id")       val userId:       String,
        @SerialName("prompt_name")   val promptName:   String,
        @SerialName("input_energy")  val inputEnergy:  Int,
        @SerialName("output_energy") val outputEnergy: Int,
        @SerialName("created_at")    val createdAt:    String? = null
    )

    /**
     * Represents one row in `prompt_usage`. The [promptId] can be either
     * a custom-prompt UUID (from `custom_prompts`) or a built-in preset slug
     * (e.g. "fix_grammar"). [isPreset] distinguishes the two cases.
     */
    @Serializable
    data class RemotePromptUsage(
        val id: String? = null,
        @SerialName("user_id")      val userId:     String,
        @SerialName("prompt_id")    val promptId:   String,
        @SerialName("is_preset")    val isPreset:   Boolean,
        @SerialName("use_count")    val useCount:   Int = 1,
        @SerialName("last_used_at") val lastUsedAt: String? = null
    )

    // ─── Actions ──────────────────────────────────────────────────────────────

    /**
     * Fetches the user's row from Supabase `energy_quotas`, creates a default row
     * if none exists, then updates the shared memory-based StateFlows.
     *
     * Call on: HomeScreen resume, post-login, keyboard service start.
     */
    suspend fun syncEnergyFromRemote(context: Context, userId: String): Unit = withContext(Dispatchers.IO) {
        if (userId.isEmpty()) {
            Log.w(TAG, "syncEnergyFromRemote: blank userId, skipping.")
            return@withContext
        }

        try {
            Log.d(TAG, "Syncing AI Energy from Supabase for user $userId...")
            val rows = supabase.from("energy_quotas")
                .select { filter { eq("user_id", userId) } }
                .decodeList<RemoteEnergyQuota>()

            val remote: RemoteEnergyQuota = if (rows.isEmpty()) {
                // No row yet — insert the default 20,000 allowance
                Log.d(TAG, "No energy_quotas row found for user $userId — creating default.")
                supabase.from("energy_quotas")
                    .insert(RemoteEnergyQuota(
                        userId = userId,
                        energyRemaining = 20_000L,
                        energyUsed = 0L
                    )) {
                        select()
                    }
                    .decodeSingle()
            } else {
                rows.first()
            }

            // Update in-memory reactive state
            _energyRemaining.value = remote.energyRemaining.toInt()
            _energyUsed.value      = remote.energyUsed.toInt()
            _energyAllowed.value   = (remote.energyRemaining + remote.energyUsed).toInt()
            _totalPrompts.value    = remote.totalPrompts.toInt()
            Log.d(TAG, "Synced AI Energy → remaining=${remote.energyRemaining}, used=${remote.energyUsed}, totalPrompts=${remote.totalPrompts}")

            // Sync today's stats from energy_transactions (lightweight — today only)
            syncTodayStats(userId)

        } catch (e: Exception) {
            Log.e(TAG, "syncEnergyFromRemote failed: ${e.message}")
        }
    }

    /** Syncs the total number of prompts and energy consumed today by querying the transactions. */
    suspend fun syncTodayStats(userId: String): Unit = withContext(Dispatchers.IO) {
        if (userId.isEmpty()) return@withContext
        try {
            val todayStartUtc = java.time.LocalDate.now()
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toString()
            Log.d(TAG, "Syncing today's prompt stats from Supabase for $userId since $todayStartUtc...")

            val rows = supabase.from("energy_transactions")
                .select {
                    filter {
                        eq("user_id", userId)
                        gte("created_at", todayStartUtc)
                    }
                }
                .decodeList<RemoteEnergyTransaction>()

            val count     = rows.size
            val energySum = rows.sumOf { it.inputEnergy + it.outputEnergy }

            _promptsToday.value = count
            _energyToday.value  = energySum
            Log.d(TAG, "Synced today's stats → promptsToday=$count, energyToday=$energySum")
        } catch (e: Exception) {
            Log.e(TAG, "syncTodayStats failed: ${e.message}")
        }
    }

    /**
     * Called immediately after a successful OpenRouter API generation.
     *
     * Updates Supabase directly and updates the reactive in-memory StateFlows.
     * Also increments [total_prompts] on energy_quotas and upserts [prompt_usage].
     *
     * @param context      Application context.
     * @param userId       The authenticated Supabase user ID.
     * @param inputEnergy  Prompt tokens × 0.75 (rounded).
     * @param outputEnergy Completion tokens × 0.75 (rounded).
     * @param promptName   Human-readable prompt label for the transaction log.
     * @param promptId     The prompt UUID (custom) or preset slug (e.g. "fix_grammar").
     * @param isPreset     True when the prompt is a built-in Synapse preset.
     */
    suspend fun consumeEnergy(
        context:      Context,
        userId:       String,
        inputEnergy:  Int,
        outputEnergy: Int,
        promptName:   String = "AI Generation",
        promptId:     String = "unknown",
        isPreset:     Boolean = false
    ): Unit = withContext(Dispatchers.IO) {
        if (userId.isEmpty()) {
            Log.w(TAG, "consumeEnergy: blank userId, skipping Supabase write.")
            return@withContext
        }

        val totalEnergy = inputEnergy + outputEnergy

        // ── Step 1: Pre-deduct in memory for instant feedback ─────────────────
        val oldRemaining     = _energyRemaining.value
        val oldUsed          = _energyUsed.value
        val oldAllowed       = _energyAllowed.value
        val oldPromptsToday  = _promptsToday.value
        val oldEnergyToday   = _energyToday.value
        val oldTotalPrompts  = _totalPrompts.value

        val tempRemaining = (oldRemaining - totalEnergy).coerceAtLeast(0)
        val tempUsed      = oldUsed + totalEnergy
        _energyRemaining.value = tempRemaining
        _energyUsed.value      = tempUsed
        _energyAllowed.value   = tempRemaining + tempUsed
        _promptsToday.value    = oldPromptsToday + 1
        _energyToday.value     = oldEnergyToday + totalEnergy
        _totalPrompts.value    = oldTotalPrompts + 1

        Log.d(TAG, "In-memory pre-deduction: -$totalEnergy energy, totalPrompts=${_totalPrompts.value}")

        // ── Step 2: Push energy update to Supabase ────────────────────────────
        try {
            val rows = supabase.from("energy_quotas")
                .select { filter { eq("user_id", userId) } }
                .decodeList<RemoteEnergyQuota>()

            if (rows.isEmpty()) {
                Log.w(TAG, "consumeEnergy: no energy_quotas row exists on Supabase.")
                return@withContext
            }

            val current      = rows.first()
            val newRemaining = (current.energyRemaining - totalEnergy).coerceAtLeast(0)
            val newUsed      = current.energyUsed + totalEnergy

            supabase.from("energy_quotas")
                .update(mapOf(
                    "energy_remaining" to newRemaining,
                    "energy_used"      to newUsed,
                    "total_prompts"    to (_totalPrompts.value.toLong())
                )) {
                    filter { eq("user_id", userId) }
                }

            Log.d(TAG, "Supabase updated → remaining=$newRemaining, used=$newUsed")

            // Update in-memory with authoritative values
            _energyRemaining.value = newRemaining.toInt()
            _energyUsed.value      = newUsed.toInt()
            _energyAllowed.value   = (newRemaining + newUsed).toInt()
            // _totalPrompts already incremented optimistically in Step 1

            // ── Step 3: Log the energy transaction ───────────────────────────
            try {
                supabase.from("energy_transactions")
                    .insert(RemoteEnergyTransaction(
                        userId       = userId,
                        promptName   = promptName,
                        inputEnergy  = inputEnergy,
                        outputEnergy = outputEnergy
                    ))
                Log.d(TAG, "Transaction logged: prompt=$promptName, in=$inputEnergy, out=$outputEnergy")

                // Re-sync today's stats in the background
                syncTodayStats(userId)
            } catch (txEx: Exception) {
                Log.w(TAG, "energy_transactions insert failed (non-critical): ${txEx.message}")
            }

            // ── Step 4: Upsert prompt_usage in Supabase ───────────────────────
            // Also keep the local Room DB in sync via MostUsedPromptDao (called
            // from OnDemandAiExecutionEngine after this function returns).
            try {
                syncPromptUsageToSupabase(userId, promptId, isPreset)
            } catch (puEx: Exception) {
                Log.w(TAG, "prompt_usage upsert failed (non-critical): ${puEx.message}")
            }

        } catch (e: Exception) {
            Log.e(TAG, "consumeEnergy remote write failed: ${e.message}")
            // Revert in-memory state to original values since the Supabase write failed
            _energyRemaining.value = oldRemaining
            _energyUsed.value      = oldUsed
            _energyAllowed.value   = oldAllowed
            _promptsToday.value    = oldPromptsToday
            _energyToday.value     = oldEnergyToday
            _totalPrompts.value    = oldTotalPrompts
        }
    }

    // syncTotalPrompts removed — total_prompts is now read directly from
    // energy_quotas.total_prompts (set on insert/update), avoiding a full
    // energy_transactions table scan on every app resume.

    /**
     * Upserts a row in `prompt_usage` for the given [promptId].
     * If the prompt was used before, increments [use_count] and refreshes [last_used_at].
     * If it is new, inserts a fresh row with [use_count] = 1.
     *
     * Uses the UNIQUE constraint on (user_id, prompt_id) to detect conflicts.
     * Called internally by [consumeEnergy] — no need to call it directly.
     */
    private suspend fun syncPromptUsageToSupabase(
        userId:   String,
        promptId: String,
        isPreset: Boolean
    ) {
        Log.d(TAG, "Syncing prompt_usage to Supabase: promptId=$promptId, isPreset=$isPreset")

        // Fetch the existing row (if any)
        val existing = supabase.from("prompt_usage")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("prompt_id", promptId)
                }
            }
            .decodeList<RemotePromptUsage>()
            .firstOrNull()

        if (existing != null) {
            // Row already exists — increment use_count
            supabase.from("prompt_usage")
                .update(mapOf(
                    "use_count"    to (existing.useCount + 1),
                    "last_used_at" to java.time.Instant.now().toString()
                )) {
                    filter {
                        eq("user_id",   userId)
                        eq("prompt_id", promptId)
                    }
                }
            Log.d(TAG, "prompt_usage incremented: promptId=$promptId, newCount=${existing.useCount + 1}")
        } else {
            // First time this prompt is used — insert fresh row
            supabase.from("prompt_usage")
                .insert(mapOf(
                    "user_id"      to userId,
                    "prompt_id"    to promptId,
                    "is_preset"    to isPreset,
                    "use_count"    to 1,
                    "last_used_at" to java.time.Instant.now().toString()
                ))
            Log.d(TAG, "prompt_usage inserted: promptId=$promptId, isPreset=$isPreset")
        }
    }
}
