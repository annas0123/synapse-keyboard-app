package com.smafty.synapsekeyboard.auth;

/**
 * AuthManager — the single source of truth for authentication state.
 *
 * Responsibilities:
 *  1. [isLoggedIn]       — Checks if a valid Supabase session exists locally.
 *  2. [signInWithGoogle] — Triggers Android Credential Manager → gets Google ID Token →
 *                          passes it to Supabase so Supabase issues its own session JWT.
 *  3. [signOut]          — Invalidates Supabase session (local + remote).
 *  4. [currentUser]      — Returns the currently authenticated [UserInfo] or null.
 *  5. [currentUserId]    — Returns just the user UUID string, or null.
 *
 * Usage: call from a ViewModel or Activity. All suspend functions run on [Dispatchers.IO].
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0015\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010\u001d\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u0010\u0017J\u000e\u0010\u001f\u001a\u0004\u0018\u00010 *\u00020\u001bH\u0002R\u0013\u0010\u0003\u001a\u0004\u0018\u00010\u00048F\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b8F\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0013\u0010\u000b\u001a\u0004\u0018\u00010\b8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\nR\u0013\u0010\r\u001a\u0004\u0018\u00010\b8F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\nR\u0013\u0010\u000f\u001a\u0004\u0018\u00010\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\nR\u0014\u0010\u0011\u001a\u00020\u00128BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006!"}, d2 = {"Lcom/smafty/synapsekeyboard/auth/AuthManager;", "", "()V", "currentUser", "Lio/github/jan/supabase/auth/user/UserInfo;", "getCurrentUser", "()Lio/github/jan/supabase/auth/user/UserInfo;", "currentUserAvatarUrl", "", "getCurrentUserAvatarUrl", "()Ljava/lang/String;", "currentUserEmail", "getCurrentUserEmail", "currentUserId", "getCurrentUserId", "currentUserName", "getCurrentUserName", "supabase", "Lio/github/jan/supabase/SupabaseClient;", "getSupabase", "()Lio/github/jan/supabase/SupabaseClient;", "isLoggedIn", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signInWithGoogle", "Lcom/smafty/synapsekeyboard/auth/AuthResult;", "context", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "signOut", "", "findActivity", "Landroid/app/Activity;", "app_debug"})
public final class AuthManager {
    @org.jetbrains.annotations.NotNull()
    public static final com.smafty.synapsekeyboard.auth.AuthManager INSTANCE = null;
    
    private AuthManager() {
        super();
    }
    
    private final io.github.jan.supabase.SupabaseClient getSupabase() {
        return null;
    }
    
    /**
     * Returns true if there is a non-null, non-expired local Supabase session.
     * Call this on app startup (on the IO dispatcher) to decide whether to show
     * the login screen or go straight to the dashboard.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isLoggedIn(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.github.jan.supabase.auth.user.UserInfo getCurrentUser() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserEmail() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentUserAvatarUrl() {
        return null;
    }
    
    private final android.app.Activity findActivity(android.content.Context $this$findActivity) {
        return null;
    }
    
    /**
     * Full Google Sign-In flow using the modern Android Credential Manager API.
     *
     * Flow:
     *  1. Build a [GetGoogleIdOption] with [BuildConfig.GOOGLE_WEB_CLIENT_ID].
     *  2. Show the Google account picker via [CredentialManager].
     *  3. Extract the Google ID token from the result credential.
     *  4. Call [supabase.auth.signInWith(IDToken)] — Supabase verifies the token
     *     with Google's public keys and creates/retrieves the user row in auth.users.
     *  5. Returns [AuthResult.Success] or [AuthResult.Error].
     *
     * @param context An Activity context — required by CredentialManager to show the picker UI.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signInWithGoogle(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smafty.synapsekeyboard.auth.AuthResult> $completion) {
        return null;
    }
    
    /**
     * Signs out the current user from Supabase (local session cleared + remote token revoked).
     * Safe to call even if the user is already signed out.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object signOut(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}