package com.smafty.synapsekeyboard.data.model

/**
 * Defines the available AI engine configurations for Synapse Keyboard.
 *
 * The underlying model IDs are intentionally abstracted away from the UI — users
 * only see branded neutral names (Synapse S1, Synapse S2, Synapse S3, Synapse S4).
 *
 * Each model carries a [factor] that reflects its relative cost compared to the
 * baseline (MiMo V2.5 = 1.00×). The factor is used in:
 *   1. **Energy calculation**: `energy = tokens × 0.75 × factor` — expensive
 *      models burn energy faster, keeping billing fair and loss-proof.
 *   2. **UI cost badge**: A human-readable [costLabel] (e.g. "💚 CHEAP") is
 *      shown in the Settings model selector so users know what they're choosing.
 *
 * ┌────────────────┬────────┬───────────────────────────┐
 * │ Display Name   │ Factor │ Cost Label                │
 * ├────────────────┼────────┼───────────────────────────┤
 * │ Synapse S1     │  0.74  │ 💚 CHEAP                  │
 * │ Synapse S2     │  1.00  │ ⚡ Baseline                │
 * │ Synapse S3     │  3.83  │ 🟡 EXPENSIVE               │
 * │ Synapse S4     │ 10.40  │ 🔴 VERY EXPENSIVE          │
 * └────────────────┴────────┴───────────────────────────┘
 */
enum class SynapseModel(
    val key: String,
    val displayName: String,
    val primaryModelId: String,
    val fallbackModelId: String,
    val description: String,
    val isRecommended: Boolean,
    /** Relative cost multiplier vs baseline (MiMo V2.5 = 1.00). */
    val factor: Float,
    /** Human-readable cost badge shown in the model selector UI. */
    val costLabel: String
) {
    S1(
        key             = "S1",
        displayName     = "Synapse S1",
        primaryModelId  = "deepseek/deepseek-v4-flash",
        fallbackModelId = "xiaomi/mimo-v2.5",
        description     = "Fast response engine optimized for casual conversations and quick replies.",
        isRecommended   = false,
        factor          = 0.74f,
        costLabel       = "💚 CHEAP"
    ),
    S2(
        key             = "S2",
        displayName     = "Synapse S2",
        primaryModelId  = "xiaomi/mimo-v2.5",
        fallbackModelId = "deepseek/deepseek-v4-flash",
        description     = "Advanced writing core designed for structured text and formatting.",
        isRecommended   = false,
        factor          = 1.00f,
        costLabel       = "⚡ Baseline"
    ),
    S3(
        key             = "S3",
        displayName     = "Synapse S3",
        primaryModelId  = "minimax/minimax-m2.5",
        fallbackModelId = "deepseek/deepseek-v4-flash",
        description     = "High-performance model for detailed analysis and long-form content.",
        isRecommended   = false,
        factor          = 3.83f,
        costLabel       = "🟡 EXPENSIVE"
    ),
    S4(
        key             = "S4",
        displayName     = "Synapse S4",
        primaryModelId  = "xiaomi/mimo-v2.5-pro",
        fallbackModelId = "deepseek/deepseek-v4-flash",
        description     = "Pro-grade model for advanced writing and complex tasks.",
        isRecommended   = false,
        factor          = 10.40f,
        costLabel       = "🔴 VERY EXPENSIVE"
    );

    companion object {
        /** Returns the matching enum from a stored key string. Defaults to S1. */
        fun fromKey(key: String?): SynapseModel {
            return entries.find { it.key == key } ?: S1
        }
    }
}
