package com.smafty.synapsekeyboard.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Premium Minimal Design System tokens (inline for splash — no theme dependency)
private val BgColor      = Color(0xFF0A0A0F)
private val VioletAccent = Color(0xFF7C5CFC)
private val TextPrimary  = Color(0xFFF0F0F5)
private val TextMuted    = Color(0xFF8888A0)

/**
 * SplashScreen — Phase 3 redesign.
 * - Pure #0A0A0F background (OLED black)
 * - Subtle violet glow ring (20% opacity) only — no green blob, no mesh
 * - Duration: 1.2s (snappier)
 * - Lottie animation preserved
 */
@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.80f) }

    // Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie_splash.json"))
    val lottieProgress by animateLottieCompositionAsState(
        composition = composition,
        iterations  = LottieConstants.IterateForever
    )

    // Subtle pulsing glow ring
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.10f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.20f,   // 20% max — subtle per spec
        targetValue   = 0.06f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(key1 = true) {
        launch {
            alpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }
        scale.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        delay(1200L)   // 1.2s — faster per spec
        onSplashFinished()
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(BgColor),   // Pure near-black — OLED optimised, NO blobs/blurs
        contentAlignment = Alignment.Center
    ) {
        // ── Content ────────────────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            // Pulsing violet glow ring behind Lottie (20% max opacity per spec)
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(VioletAccent.copy(alpha = pulseAlpha))
                )
                LottieAnimation(
                    composition = composition,
                    progress    = { lottieProgress },
                    modifier    = Modifier.size(148.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name
            Text(
                text          = "Synapse",
                style         = MaterialTheme.typography.headlineLarge,
                fontWeight    = FontWeight.Bold,
                color         = TextPrimary,
                textAlign     = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline — muted, spaced caps
            Text(
                text          = "AI  ·  KEYBOARD",
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Medium,
                letterSpacing = 4.sp,
                color         = TextMuted,
                textAlign     = TextAlign.Center
            )
        }
    }
}
