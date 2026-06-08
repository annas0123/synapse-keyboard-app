package com.smafty.synapsekeyboard.auth

import com.smafty.synapsekeyboard.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * SupabaseClientProvider — singleton that holds the single [io.github.jan.supabase.SupabaseClient]
 * instance used across the entire app.
 *
 * Keys are injected at compile-time via BuildConfig (read from local.properties which is never
 * committed to source control). This ensures the anon key never lives in plain-text source code.
 *
 * Installed plugins:
 *   • Auth       — handles Google OAuth, session storage, token refresh
 *   • Postgrest  — type-safe REST access to Supabase tables (profiles, word_quotas)
 */
object SupabaseClientProvider {

    val client by lazy {
        createSupabaseClient(
            supabaseUrl  = BuildConfig.SUPABASE_URL,
            supabaseKey  = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                // Store the session in Android EncryptedSharedPreferences automatically
                // (handled by the Supabase SDK's built-in session manager)
            }
            install(Postgrest)
        }
    }
}
