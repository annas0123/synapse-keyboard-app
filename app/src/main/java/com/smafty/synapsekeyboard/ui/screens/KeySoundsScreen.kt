package com.smafty.synapsekeyboard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.ui.keyboard.KeySoundEngine
import com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import com.smafty.synapsekeyboard.ui.theme.GlassmorphismColor
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import com.smafty.synapsekeyboard.ui.theme.TextColor

// ---------------------------------------------------------------------------
// Key Sounds Configuration Screen — Premium redesign
// ---------------------------------------------------------------------------
@Composable
fun KeySoundsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("synapse_prefs", android.content.Context.MODE_PRIVATE) }

    var soundEnabled by remember { mutableStateOf(KeySoundEngine.activePreset != KeySoundPreset.NONE) }
    var selectedPreset by remember {
        mutableStateOf(
            if (KeySoundEngine.activePreset == KeySoundPreset.NONE) KeySoundPreset.MODERN_DIGITAL
            else KeySoundEngine.activePreset
        )
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val cyanAccent = Color(0xFF06B6D4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // ── Header ─────────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { -24 }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(4.dp, RoundedCornerShape(13.dp), spotColor = ElectricPurple.copy(0.3f))
                        .clip(RoundedCornerShape(13.dp))
                        .background(GlassmorphismColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = onBack
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint               = TextColor,
                        modifier           = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(listOf(cyanAccent, cyanAccent.copy(0f)))
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text       = "Key Sounds",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextColor
                    )
                    Text(
                        text  = "Choose your click experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGrey.copy(alpha = 0.70f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Enable Toggle Card ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 80)) + slideInVertically(tween(500, 80)) { 40 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = cyanAccent.copy(0.25f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                cyanAccent.copy(alpha = if (soundEnabled) 0.14f else 0.04f),
                                ElectricPurple.copy(alpha = if (soundEnabled) 0.08f else 0.02f)
                            )
                        )
                    )
                    .background(GlassmorphismColor)
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (soundEnabled) cyanAccent.copy(alpha = 0.18f)
                                else MutedGrey.copy(alpha = 0.08f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = if (soundEnabled) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                            contentDescription = null,
                            tint               = if (soundEnabled) cyanAccent else MutedGrey,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Key Sounds",
                            color      = TextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text  = if (soundEnabled) "Active: ${selectedPreset.displayName}" else "Tap to enable sounds",
                            color = if (soundEnabled) cyanAccent else MutedGrey.copy(alpha = 0.55f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked       = soundEnabled,
                        onCheckedChange = { enabled ->
                            soundEnabled = enabled
                            val preset = if (enabled) selectedPreset else KeySoundPreset.NONE
                            KeySoundEngine.savePreset(prefs, preset)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor    = Color.White,
                            checkedTrackColor    = cyanAccent,
                            uncheckedThumbColor  = MutedGrey,
                            uncheckedTrackColor  = MutedGrey.copy(alpha = 0.18f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Section Label ──────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 140))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.padding(start = 4.dp, bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(cyanAccent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text          = "SOUND PRESETS",
                    style         = MaterialTheme.typography.labelSmall,
                    color         = MutedGrey.copy(alpha = 0.55f),
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }

        // ── Preset Cards ───────────────────────────────────────────────────────
        val presets = KeySoundPreset.entries.filter { it != KeySoundPreset.NONE }
        presets.forEachIndexed { index, preset ->
            val delay = 160 + index * 45
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(450, delay)) + slideInVertically(tween(450, delay)) { 30 }
            ) {
                SoundPresetCard(
                    preset       = preset,
                    isSelected   = preset == selectedPreset,
                    soundEnabled = soundEnabled,
                    accentColor  = cyanAccent,
                    onSelect     = {
                        selectedPreset = preset
                        if (soundEnabled) KeySoundEngine.savePreset(prefs, preset)
                    },
                    onPreview = { KeySoundEngine.preview(preset) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(36.dp))
    }
}

// ---------------------------------------------------------------------------
// Individual preset card — left accent strip design, no harsh borders
// ---------------------------------------------------------------------------
@Composable
private fun SoundPresetCard(
    preset: KeySoundPreset,
    isSelected: Boolean,
    soundEnabled: Boolean,
    accentColor: Color,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    val stripColor = if (isSelected) accentColor else MutedGrey.copy(alpha = 0.25f)
    val elevDp     by animateDpAsState(
        targetValue  = if (isSelected) 5.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label        = "elev"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevDp, RoundedCornerShape(18.dp), spotColor = accentColor.copy(if (isSelected) 0.3f else 0f))
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(listOf(accentColor.copy(0.10f), accentColor.copy(0.03f)))
                else
                    Brush.linearGradient(listOf(GlassmorphismColor, GlassmorphismColor))
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onSelect
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .background(
                    Brush.verticalGradient(listOf(stripColor, stripColor.copy(alpha = 0.25f)))
                )
        )
        Spacer(modifier = Modifier.width(14.dp))

        // Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) accentColor.copy(alpha = 0.16f)
                    else MutedGrey.copy(alpha = 0.07f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector        = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint               = accentColor,
                    modifier           = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector        = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint               = MutedGrey.copy(alpha = 0.45f),
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f).padding(vertical = 14.dp)) {
            Text(
                text       = preset.displayName,
                color      = if (isSelected) accentColor else TextColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize   = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = preset.description,
                color    = MutedGrey.copy(alpha = 0.65f),
                fontSize = 11.sp
            )
        }

        // Preview button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) accentColor.copy(alpha = 0.20f)
                    else MutedGrey.copy(alpha = 0.07f)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onPreview
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Rounded.PlayArrow,
                contentDescription = "Preview ${preset.displayName}",
                tint               = if (isSelected) accentColor else MutedGrey.copy(alpha = 0.5f),
                modifier           = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}
