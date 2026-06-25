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
import com.smafty.synapsekeyboard.ui.theme.ThemeManager

// ── Monochrome design tokens — all values resolve to ThemeManager (single source of truth).
private val KS_Bg: Color        get() = ThemeManager.currentTheme.background
private val KS_Surface: Color   get() = ThemeManager.currentTheme.surface
private val KS_Border: Color    get() = ThemeManager.currentTheme.border
private val KS_Primary: Color   get() = ThemeManager.currentTheme.primary
private val KS_OnPrimary: Color get() = ThemeManager.currentTheme.background
private val KS_Text: Color      get() = ThemeManager.currentTheme.textPrimary
private val KS_Muted: Color     get() = ThemeManager.currentTheme.textSecondary

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

    val cyanAccent = KS_Primary   // monochrome accent → primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KS_Bg)
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KS_Surface)
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
                        tint               = KS_Text,
                        modifier           = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text       = "Key Sounds",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = KS_Text
                    )
                    Text(
                        text  = "Choose your click experience",
                        fontSize = 13.sp,
                        color = KS_Muted
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(KS_Surface)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(
                                if (soundEnabled) cyanAccent.copy(alpha = 0.15f)
                                else KS_Border
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = if (soundEnabled) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                            contentDescription = null,
                            tint               = if (soundEnabled) cyanAccent else KS_Muted,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Key Sounds",
                            color      = KS_Text,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text  = if (soundEnabled) "Active: ${selectedPreset.displayName}" else "Tap to enable sounds",
                            color = if (soundEnabled) cyanAccent else KS_Muted,
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
                            checkedThumbColor    = KS_OnPrimary,
                            checkedTrackColor    = cyanAccent,
                            uncheckedThumbColor  = KS_Muted,
                            uncheckedTrackColor  = KS_Border
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
            Text(
                text          = "SOUND PRESETS",
                fontSize      = 12.sp,
                color         = KS_Muted,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                modifier      = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) accentColor.copy(0.10f)
                else KS_Surface
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onSelect
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip (4dp)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .background(if (isSelected) accentColor else KS_Border)
        )
        Spacer(modifier = Modifier.width(14.dp))

        // Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) accentColor.copy(alpha = 0.16f)
                    else KS_Muted.copy(alpha = 0.07f)
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
                    tint               = KS_Muted.copy(alpha = 0.45f),
                    modifier           = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f).padding(vertical = 14.dp)) {
            Text(
                text       = preset.displayName,
                color      = if (isSelected) accentColor else KS_Text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize   = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = preset.description,
                color    = KS_Muted,
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
                    else KS_Muted.copy(alpha = 0.07f)
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
                tint               = if (isSelected) accentColor else KS_Muted.copy(alpha = 0.5f),
                modifier           = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}
