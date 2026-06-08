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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.75f) }

    // Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie_splash.json"))
    val lottieProgress by animateLottieCompositionAsState(
        composition  = composition,
        iterations   = LottieConstants.IterateForever
    )

    // Pulsing glow ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue  = 0.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(key1 = true) {
        launch {
            alpha.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        }
        scale.animateTo(1f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        delay(1600L)
        onSplashFinished()
    }

    Box(
        modifier          = Modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center
    ) {
        // ── Layered ambient background ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSlate)
        )
        // Top-left purple blob
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = (-80).dp, y = (-120).dp)
                .clip(CircleShape)
                .background(ElectricPurple.copy(alpha = 0.10f))
                .align(Alignment.TopStart)
        )
        // Bottom-right teal blob
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 80.dp, y = 100.dp)
                .clip(CircleShape)
                .background(EmeraldGreen.copy(alpha = 0.08f))
                .align(Alignment.BottomEnd)
        )
        // Center radial highlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricPurple.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.42f),
                        radius = 700f
                    )
                )
        )

        // ── Content ────────────────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            // Pulsing glow ring behind Lottie
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(ElectricPurple.copy(alpha = pulseAlpha))
                )
                LottieAnimation(
                    composition = composition,
                    progress    = { lottieProgress },
                    modifier    = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text          = "Synapse",
                style         = MaterialTheme.typography.displayMedium,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                textAlign     = TextAlign.Center,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline with gradient
            Text(
                text          = "AI  ·  KEYBOARD",
                fontSize      = 13.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 5.sp,
                color         = ElectricPurple.copy(alpha = 0.80f),
                textAlign     = TextAlign.Center
            )
        }
    }
}
