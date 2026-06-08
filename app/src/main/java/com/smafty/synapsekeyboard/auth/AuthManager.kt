package com.smafty.synapsekeyboard.auth

import android.app.Activity
import android.content.ContextWrapper
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.smafty.synapsekeyboard.BuildConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AuthManager"

/**
 * AuthManager — the single source of truth for authentication state.
 *
 * Responsibilities:
 *   1. [isLoggedIn]       — Checks if a valid Supabase session exists locally.
 *   2. [signInWithGoogle] — Triggers Android Credential Manager → gets Google ID Token →
 *                           passes it to Supabase so Supabase issues its own session JWT.
 *   3. [signOut]          — Invalidates Supabase session (local + remote).
 *   4. [currentUser]      — Returns the currently authenticated [UserInfo] or null.
 *   5. [currentUserId]    — Returns just the user UUID string, or null.
 *
 * Usage: call from a ViewModel or Activity. All suspend functions run on [Dispatchers.IO].
 */
object AuthManager {

    private val supabase get() = SupabaseClientProvider.client

    // ── Session check ─────────────────────────────────────────────────────────

    /**
     * Returns true if there is a non-null, non-expired local Supabase session.
     * Call this on app startup (on the IO dispatcher) to decide whether to show
     * the login screen or go straight to the dashboard.
     */
    suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.auth.loadFromStorage()          // Restores session from disk (no network)
            supabase.auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            Log.w(TAG, "isLoggedIn check failed: ${e.message}")
            false
        }
    }

    // ── Current user accessors ────────────────────────────────────────────────

    val currentUser: UserInfo?
        get() = supabase.auth.currentUserOrNull()

    val currentUserId: String?
        get() = currentUser?.id

    val currentUserEmail: String?
        get() = currentUser?.email

    val currentUserName: String?
        get() = currentUser?.userMetadata?.get("full_name")?.toString()
            ?.trim('"')                              // Supabase wraps JSON strings in quotes

    val currentUserAvatarUrl: String?
        get() = currentUser?.userMetadata?.get("avatar_url")?.toString()
            ?.trim('"')

    // ── Helper to find Activity context ───────────────────────────────────────

    private fun Context.findActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }

    // ── Sign in ───────────────────────────────────────────────────────────────

    /**
     * Full Google Sign-In flow using the modern Android Credential Manager API.
     *
     * Flow:
     *   1. Build a [GetGoogleIdOption] with [BuildConfig.GOOGLE_WEB_CLIENT_ID].
     *   2. Show the Google account picker via [CredentialManager].
     *   3. Extract the Google ID token from the result credential.
     *   4. Call [supabase.auth.signInWith(IDToken)] — Supabase verifies the token
     *      with Google's public keys and creates/retrieves the user row in auth.users.
     *   5. Returns [AuthResult.Success] or [AuthResult.Error].
     *
     * @param context An Activity context — required by CredentialManager to show the picker UI.
     */
    suspend fun signInWithGoogle(context: Context): AuthResult = withContext(Dispatchers.IO) {
        try {
            val activity = context.findActivity() 
                ?: throw IllegalArgumentException("An Activity context is required for CredentialManager.")
            val credentialManager = CredentialManager.create(activity)

            // Build the Google ID token request
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)   // Show ALL Google accounts, not just authorised ones
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)             // Always show the picker — no silent sign-in
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Show the account picker (suspends until user selects or cancels)
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            // Extract Google ID token
            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val googleIdToken = googleIdTokenCredential.idToken

            // Exchange with Supabase
            supabase.auth.signInWith(IDToken) {
                idToken   = googleIdToken
                provider  = Google
            }

            Log.i(TAG, "Google sign-in success: user=${currentUserId}")
            AuthResult.Success

        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled Google sign-in")
            AuthResult.Cancelled

        } catch (e: Throwable) {
            Log.e(TAG, "Google sign-in failed", e)
            try {
                val file = java.io.File(context.filesDir, "last_crash.txt")
                val writer = java.io.PrintWriter(java.io.FileWriter(file))
                e.printStackTrace(writer)
                writer.flush()
                writer.close()
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to write crash to file", ex)
            }
            AuthResult.Error(e.message ?: "Sign-in failed. Please try again.")
        }
    }

    // ── Sign out ──────────────────────────────────────────────────────────────

    /**
     * Signs out the current user from Supabase (local session cleared + remote token revoked).
     * Safe to call even if the user is already signed out.
     */
    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signOut()
            Log.i(TAG, "User signed out successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Sign-out error (session was probably already invalid): ${e.message}")
            // Swallow the error — the local session is cleared regardless
        }
    }
}

// ── Result sealed class ───────────────────────────────────────────────────────

sealed class AuthResult {
    data object Success   : AuthResult()
    data object Cancelled : AuthResult()
    data class  Error(val message: String) : AuthResult()
}
