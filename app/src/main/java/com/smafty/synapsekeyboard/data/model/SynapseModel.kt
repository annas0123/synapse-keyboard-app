package com.smafty.synapsekeyboard.data.model

/**
 * Defines the available AI engine configurations for Synapse Keyboard.
 *
 * The underlying model IDs are intentionally abstracted away from the UI — users
 * only see branded neutral names (Synapse S1, Synapse S2, Synapse S3, Synapse S4).
 *
 * Energy consumption is automatically calculated based on the actual API cost
 * returned by OpenRouter, keeping the billing 100% loss-proof and fair.
 */
enum class SynapseModel(
    val key: String,
    val displayName: String,
    val primaryModelId: String,
    val fallbackModelId: String,
    val description: String,
    val isRecommended: Boolean
) {
    S1(
        key             = "S1",
        displayName     = "Synapse S1",
        primaryModelId  = "deepseek/deepseek-v4-flash",
        fallbackModelId = "qwen/qwen3.5-flash-02-23",
        description     = "Fast response engine optimized for casual conversations and quick replies.",
        isRecommended   = false
    ),
    S2(
        key             = "S2",
        displayName     = "Synapse S2",
        primaryModelId  = "xiaomi/mimo-v2.5",
        fallbackModelId = "deepseek/deepseek-v4-flash",
        description     = "Advanced writing core designed for structured text and formatting.",
        isRecommended   = false
    ),
    S3(
        key             = "S3",
        displayName     = "Synapse S3",
        primaryModelId  = "qwen/qwen3.5-flash-02-23",
        fallbackModelId = "deepseek/deepseek-v4-flash",
        description     = "Efficient language core suited for daily communication and editing.",
        isRecommended   = false
    ),
    S4(
        key             = "S4",
        displayName     = "Synapse S4",
        primaryModelId  = "qwen/qwen3-235b-a22b-2507",
        fallbackModelId = "qwen/qwen3.5-flash-02-23",
        description     = "High-capacity reasoning engine built for complex texts and drafting.",
        isRecommended   = false
    );

    companion object {
        /** Returns the matching enum from a stored key string. Defaults to S1. */
        fun fromKey(key: String?): SynapseModel {
            return entries.find { it.key == key } ?: S1
        }
    }
}
