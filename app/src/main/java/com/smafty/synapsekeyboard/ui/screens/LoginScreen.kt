package com.smafty.synapsekeyboard.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.auth.AuthManager
import com.smafty.synapsekeyboard.auth.AuthResult
import kotlinx.coroutines.launch

// ── Premium Minimal design tokens ─────────────────────────────────────────────
private val LG_Bg          = Color(0xFF0A0A0F)
private val LG_Surface     = Color(0xFF141420)
private val LG_Border      = Color(0xFF2A2A3A)
private val LG_Violet      = Color(0xFF7C5CFC)
private val LG_TextPrimary = Color(0xFFF0F0F5)
private val LG_TextSecond  = Color(0xFF8888A0)

/**
 * LoginScreen — Phase 3 redesign.
 * - #0A0A0F background (no blobs, no blurs, no glassmorphism)
 * - Logo: solid #141420 card with 1dp #2A2A3A border (removed glow ring)
 * - Value props: simple icon + text rows, no glass card
 * - Google button: White bg, dark text, 12dp radius
 * - Backend (AuthManager) fully preserved
 */
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }

    var isLoading      by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.80f) }

    LaunchedEffect(Unit) {
        contentVisible = true
        logoAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
    }

    BackHandler { (context as? Activity)?.finish() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LG_Bg)  // Solid near-black — NO blobs, NO blur, NO mesh
    ) {
        // ── Main content ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo — solid card, no glow ────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
                    .clip(RoundedCornerShape(24.dp))
                    .background(LG_Surface)
                    .border(1.dp, LG_Border, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.AutoAwesome,
                    contentDescription = "Synapse AI",
                    tint               = LG_Violet,
                    modifier           = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Brand wordmark ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(500, 150)) + slideInVertically(tween(500, 150)) { 20 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text          = "Synapse",
                        fontSize      = 28.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = LG_TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text          = "AI  ·  KEYBOARD",
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Medium,
                        color         = LG_TextSecond,
                        letterSpacing = 3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // ── Value props — simple rows, NO glass card ──────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(500, 280)) + slideInVertically(tween(500, 280)) { 20 }
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ValuePropRow(Icons.Rounded.Bolt,        Color(0xFFFBBF24), "AI writing assistant built into every keyboard")
                    // Subtle divider
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(LG_Border))
                    ValuePropRow(Icons.Rounded.Lock,         Color(0xFF34D399), "Your data stays secure with Google Auth")
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(LG_Border))
                    ValuePropRow(Icons.Rounded.CardGiftcard, LG_Violet,         "Start free — 20,000 energy credits on us")
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // ── Sign-In Button ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(500, 400)) + slideInVertically(tween(500, 400)) { 20 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick  = {
                            if (!isLoading) {
                                isLoading = true
                                scope.launch {
                                    val result = AuthManager.signInWithGoogle(context)
                                    when (result) {
                                        is AuthResult.Success   -> onLoginSuccess()
                                        is AuthResult.Cancelled -> isLoading = false
                                        is AuthResult.Error     -> {
                                            isLoading = false
                                            snackbarHost.showSnackbar(
                                                result.message.ifBlank { "Sign-in failed. Check your connection and try again." }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp),   // 12dp per spec
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor   = Color(0xFF1F1F1F)
                        ),
                        enabled  = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                color       = LG_Violet,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Signing in…", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color(0xFF444444))
                        } else {
                            GoogleGLogo()
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Continue with Google", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color(0xFF1F1F1F))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text       = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
                        color      = LG_TextSecond.copy(alpha = 0.60f),
                        textAlign  = TextAlign.Center,
                        lineHeight = 18.sp,
                        fontSize   = 11.sp
                    )
                }
            }
        }

        // ── Snackbar ──────────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHost,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData   = data,
                containerColor = LG_Surface,
                contentColor   = LG_TextPrimary,
                actionColor    = LG_Violet,
                shape          = RoundedCornerShape(12.dp)
            )
        }
    }
}

// ── Value prop row — icon + text, NO glass card ──────────────────────────────
@Composable
private fun ValuePropRow(
    icon: ImageVector,
    tint: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text       = text,
            color      = LG_TextSecond,
            fontSize   = 14.sp,
            lineHeight = 20.sp,
            modifier   = Modifier.weight(1f)
        )
    }
}

// ── Google "G" logo (pixel-grid approach) ─────────────────────────────────────
@Composable
private fun GoogleGLogo(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(22.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row {
                    Box(Modifier.size(5.dp).background(Color(0xFF4285F4)))
                    Box(Modifier.size(5.dp).background(Color(0xFFEA4335)))
                }
                Row {
                    Box(Modifier.size(5.dp).background(Color(0xFFFBBC05)))
                    Box(Modifier.size(5.dp).background(Color(0xFF34A853)))
                }
            }
        }
    }
}
