package com.smafty.synapsekeyboard.data.model

/**
 * Defines the two available AI engine configurations for Synapse Keyboard.
 *
 * Energy formula (same for both models, no factor):
 *   Input Energy  = input_tokens  × 0.75
 *   Output Energy = output_tokens × 0.75
 *   Total Energy  = Input Energy  + Output Energy
 *
 * ┌─────────────────────┬──────────────────────────────────────────────┐
 * │ Display Name        │ Model ID                                     │
 * ├─────────────────────┼──────────────────────────────────────────────┤
 * │ DeepSeek V4 Flash   │ deepseek/deepseek-v4-flash                   │
 * │ MiMo V2.5           │ xiaomi/mimo-v2.5                             │
 * └─────────────────────┴──────────────────────────────────────────────┘
 */
enum class SynapseModel(
    val key: String,
    val displayName: String,
    val primaryModelId: String,
    val description: String,
    val isRecommended: Boolean
) {
    DEEPSEEK(
        key            = "DEEPSEEK",
        displayName    = "Synapse S1",
        primaryModelId = "deepseek/deepseek-v4-flash",
        description    = "Fast, efficient model optimized for quick replies and casual writing.",
        isRecommended  = false
    ),
    MIMO(
        key            = "MIMO",
        displayName    = "Synapse S2",
        primaryModelId = "xiaomi/mimo-v2.5",
        description    = "Advanced writing model designed for structured text and formatting.",
        isRecommended  = true
    );

    companion object {
        /** Returns the matching enum from a stored key string. Defaults to DEEPSEEK. */
        fun fromKey(key: String?): SynapseModel {
            return entries.find { it.key == key } ?: DEEPSEEK
        }
    }
}
