package com.smafty.synapsekeyboard.data.local.repository

import com.smafty.synapsekeyboard.auth.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Pure Supabase Repository for Custom Prompts.
 * Room DB is completely bypassed/removed for this feature.
 * Uses Supabase Kotlin SDK v3 API (supabase.from("table") style).
 *
 * Auth note: We read the user ID directly from the Supabase auth session
 * (after ensuring loadFromStorage() has run) so we never get a null userId
 * race condition.
 */
object CustomPromptRepository {

    private val supabase = SupabaseClientProvider.client
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _prompts = MutableStateFlow<List<RemoteCustomPrompt>>(emptyList())
    val prompts: StateFlow<List<RemoteCustomPrompt>> = _prompts

    init {
        repositoryScope.launch {
            try {
                // Ensure initialization completes
                supabase.auth.awaitInitialization()
            } catch (e: Exception) {
                android.util.Log.w("CustomPromptRepository", "awaitInitialization failed: ${e.message}")
            }
            supabase.auth.sessionStatus.collect { status ->
                android.util.Log.d("CustomPromptRepository", "sessionStatus flow emitted: $status")
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val userId = status.session.user?.id
                        android.util.Log.d("CustomPromptRepository", "Session Authenticated for user $userId. Triggering reactive fetch.")
                        fetchPrompts()
                    }
                    is SessionStatus.NotAuthenticated -> {
                        android.util.Log.d("CustomPromptRepository", "Session NotAuthenticated. Clearing custom prompts.")
                        _prompts.value = emptyList()
                    }
                    else -> {}
                }
            }
        }
    }

    /** Resolves the current user ID, loading from storage if needed. */
    private suspend fun resolveUserId(): String? {
        // Try fast path first
        var userId = supabase.auth.currentUserOrNull()?.id
        if (userId != null) return userId

        // Session might not be loaded yet — load it from storage
        try {
            supabase.auth.loadFromStorage()
            supabase.auth.awaitInitialization()
        } catch (e: Exception) {
            android.util.Log.w("CustomPromptRepository", "loadFromStorage failed: ${e.message}")
        }

        userId = supabase.auth.currentUserOrNull()?.id
        android.util.Log.d("CustomPromptRepository", "resolveUserId: $userId")
        return userId
    }

    suspend fun fetchPrompts() = withContext(Dispatchers.IO) {
        val userId = resolveUserId()
        android.util.Log.d("CustomPromptRepository", "fetchPrompts called. currentUserId = $userId")
        if (userId == null) {
            android.util.Log.e("CustomPromptRepository", "fetchPrompts aborted: user not logged in")
            return@withContext
        }
        try {
            val remote = supabase.from("custom_prompts")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<RemoteCustomPrompt>()

            android.util.Log.d("CustomPromptRepository", "Fetched ${remote.size} prompts from Supabase for userId=$userId")
            _prompts.value = remote
        } catch (e: Exception) {
            android.util.Log.e("CustomPromptRepository", "Error in fetchPrompts", e)
        }
    }

    suspend fun addPrompt(title: String, prompt: String) = withContext(Dispatchers.IO) {
        val userId = resolveUserId()
        android.util.Log.d("CustomPromptRepository", "addPrompt called: title = $title, userId = $userId")
        if (userId == null) return@withContext
        try {
            val inserted = supabase.from("custom_prompts")
                .insert(
                    mapOf(
                        "title" to title,
                        "prompt" to prompt,
                        "user_id" to userId
                    )
                ) {
                    select()
                }
                .decodeSingle<RemoteCustomPrompt>()

            android.util.Log.d("CustomPromptRepository", "Successfully inserted prompt: id = ${inserted.id}")
            _prompts.value = _prompts.value + inserted
        } catch (e: Exception) {
            android.util.Log.e("CustomPromptRepository", "Error in addPrompt", e)
        }
    }

    suspend fun updatePrompt(id: String, newTitle: String, newPrompt: String) = withContext(Dispatchers.IO) {
        android.util.Log.d("CustomPromptRepository", "updatePrompt called: id = $id, title = $newTitle")
        try {
            val updated = supabase.from("custom_prompts")
                .update(
                    mapOf(
                        "title" to newTitle,
                        "prompt" to newPrompt
                    )
                ) {
                    select()
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<RemoteCustomPrompt>()

            android.util.Log.d("CustomPromptRepository", "Successfully updated prompt: id = ${updated.id}")
            _prompts.value = _prompts.value.map { if (it.id == id) updated else it }
        } catch (e: Exception) {
            android.util.Log.e("CustomPromptRepository", "Error in updatePrompt", e)
        }
    }

    suspend fun deletePrompt(id: String) = withContext(Dispatchers.IO) {
        android.util.Log.d("CustomPromptRepository", "deletePrompt called: id = $id")
        try {
            supabase.from("custom_prompts").delete {
                filter {
                    eq("id", id)
                }
            }
            android.util.Log.d("CustomPromptRepository", "Successfully deleted prompt: id = $id")
            _prompts.value = _prompts.value.filter { it.id != id }
        } catch (e: Exception) {
            android.util.Log.e("CustomPromptRepository", "Error in deletePrompt", e)
        }
    }

    suspend fun deleteAllPrompts() = withContext(Dispatchers.IO) {
        val userId = resolveUserId()
        android.util.Log.d("CustomPromptRepository", "deleteAllPrompts called for userId = $userId")
        if (userId == null) return@withContext
        try {
            supabase.from("custom_prompts").delete {
                filter {
                    eq("user_id", userId)
                }
            }
            android.util.Log.d("CustomPromptRepository", "Successfully deleted all prompts for userId=$userId")
            _prompts.value = emptyList()
        } catch (e: Exception) {
            android.util.Log.e("CustomPromptRepository", "Error in deleteAllPrompts", e)
        }
    }
}

@Serializable
data class RemoteCustomPrompt(
    val id: String,
    @SerialName("user_id") val userId: String,
    val title: String,
    val prompt: String,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)
