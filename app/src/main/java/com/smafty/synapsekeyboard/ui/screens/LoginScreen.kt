package com.smafty.synapsekeyboard.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.auth.AuthManager
import com.smafty.synapsekeyboard.auth.AuthResult
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import com.smafty.synapsekeyboard.ui.theme.GlassmorphismColor
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Login Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context      = LocalContext.current
    val scope        = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }

    var isLoading      by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.65f) }

    LaunchedEffect(Unit) {
        contentVisible = true
        logoAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    BackHandler { (context as? Activity)?.finish() }

    // Pulsing glow animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue   = 0.35f,
        targetValue    = 0.65f,
        animationSpec  = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label          = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
    ) {
        // ── Background: layered radial glows ─────────────────────────────────
        // Primary purple top glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricPurple.copy(alpha = 0.28f),
                            ElectricPurple.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.15f),
                        radius = 900f
                    )
                )
        )
        // Cyan accent streak
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00D4FF).copy(alpha = 0.08f), Color.Transparent),
                        radius = 500f
                    )
                )
        )
        // Bottom emerald reflection
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(EmeraldGreen.copy(alpha = 0.10f), Color.Transparent),
                        radius = 600f
                    )
                )
        )

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

            // ── Logo with animated glow ring ─────────────────────────────────
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing outer glow ring
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(ElectricPurple.copy(alpha = glowPulse * 0.18f))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                listOf(ElectricPurple.copy(alpha = glowPulse), ElectricPurple.copy(alpha = glowPulse * 0.3f))
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                )
                // Inner icon box
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(ElectricPurple.copy(alpha = 0.55f), Color(0xFF1A0A3C))
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                listOf(ElectricPurple.copy(alpha = 0.90f), ElectricPurple.copy(alpha = 0.25f))
                            ),
                            shape = RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.AutoAwesome,
                        contentDescription = "Synapse AI",
                        tint               = Color.White,
                        modifier           = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Brand wordmark ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(600, 200)) + slideInVertically(tween(600, 200)) { 28 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Synapse",
                        style      = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    // Gradient subtitle
                    Text(
                        text = "AI · Keyboard",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color      = ElectricPurple.copy(alpha = 0.90f),
                        letterSpacing = 4.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // ── Value props glass card ────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(600, 400)) + slideInVertically(tween(600, 400)) { 28 }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(GlassmorphismColor)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(Color.White.copy(0.14f), ElectricPurple.copy(0.18f), Color.White.copy(0.06f))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ValuePropRow(Icons.Rounded.Bolt,        Color(0xFFF59E0B), "AI writing assistant built into every keyboard")
                    ValuePropDivider()
                    ValuePropRow(Icons.Rounded.Lock,         Color(0xFF10B981), "Your data stays secure with Google Auth")
                    ValuePropDivider()
                    ValuePropRow(Icons.Rounded.CardGiftcard, ElectricPurple,    "Start free — 20,000 energy credits on us")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Sign-In Button ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter   = fadeIn(tween(600, 600)) + slideInVertically(tween(600, 600)) { 28 }
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
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape    = RoundedCornerShape(18.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor   = Color(0xFF1F1F1F)
                        ),
                        enabled  = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(22.dp),
                                color       = ElectricPurple,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Signing in…", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF444444))
                        } else {
                            GoogleGLogo()
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF1F1F1F))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text      = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
                        style     = MaterialTheme.typography.bodySmall,
                        color     = MutedGrey.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center,
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
                containerColor = Color(0xFF2D1B4E),
                contentColor   = Color.White,
                actionColor    = ElectricPurple,
                shape          = RoundedCornerShape(14.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Thin divider between value props
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ValuePropDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.White.copy(0.08f), Color.Transparent)
                )
            )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Google "G" logo
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GoogleGLogo(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(24.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.size(22.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
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
}

// ─────────────────────────────────────────────────────────────────────────────
// Value proposition row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ValuePropRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.radialGradient(listOf(tint.copy(alpha = 0.22f), tint.copy(alpha = 0.06f)))
                )
                .border(1.dp, tint.copy(alpha = 0.22f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(19.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text       = text,
            color      = MutedGrey.copy(alpha = 0.90f),
            fontSize   = 14.sp,
            lineHeight = 20.sp,
            modifier   = Modifier.weight(1f)
        )
    }
}
