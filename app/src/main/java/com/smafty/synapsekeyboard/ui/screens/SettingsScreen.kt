package com.smafty.synapsekeyboard.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.smafty.synapsekeyboard.data.model.SynapseModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.auth.AuthManager
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import com.smafty.synapsekeyboard.ui.theme.GlassmorphismColor
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import com.smafty.synapsekeyboard.ui.theme.ThemeManager
import com.smafty.synapsekeyboard.ui.theme.AppThemePreset
import com.smafty.synapsekeyboard.ui.theme.TextColor
import com.smafty.synapsekeyboard.ui.keyboard.KeySoundEngine
import com.smafty.synapsekeyboard.ui.keyboard.KeySoundPreset
import com.smafty.synapsekeyboard.ui.keyboard.HapticEngine
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Settings Screen — UI layer upgraded with premium theme design, backend logic preserved.
// ---------------------------------------------------------------------------
@Composable
fun SettingsScreen(
    onNavigateToKeyboardTest: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null,
    onNavigateToKeySounds: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val prefs   = remember { context.getSharedPreferences("synapse_prefs", android.content.Context.MODE_PRIVATE) }

    var soundEnabled      by remember { mutableStateOf(KeySoundEngine.activePreset != KeySoundPreset.NONE) }
    var vibrationEnabled  by remember { mutableStateOf(HapticEngine.enabled) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showThemeDialog   by remember { mutableStateOf(false) }

    var selectedModelKey by remember {
        mutableStateOf(prefs.getString("synapse_selected_model", SynapseModel.S1.key) ?: SynapseModel.S1.key)
    }

    val userName  = AuthManager.currentUserName  ?: "Guest"
    val userEmail = AuthManager.currentUserEmail ?: "Not signed in"

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // ── Header with accent strip ──────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { -24 }
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ElectricPurple, ElectricPurple.copy(alpha = 0.0f))
                            )
                        )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text       = "Settings",
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextColor
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text  = "Preferences & account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGrey.copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Section: Account ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 60)) + slideInVertically(tween(500, 60)) { 40 }
        ) {
            SettingsSection(title = "Account") {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(8.dp, CircleShape, spotColor = ElectricPurple.copy(alpha = 0.35f))
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        ElectricPurple.copy(alpha = 0.22f),
                                        ElectricPurple.copy(alpha = 0.06f)
                                    )
                                )
                            )
                            .border(1.5.dp, ElectricPurple.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                            color      = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = userName,
                            color      = TextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text  = userEmail,
                            color = MutedGrey.copy(alpha = 0.65f),
                            fontSize = 12.sp
                        )
                    }
                }
                SettingsDivider()
                ActionSettingsRow(
                    icon        = Icons.Rounded.ExitToApp,
                    iconTint    = Color(0xFFEF4444),
                    label       = "Sign Out",
                    description = "Sign out of your Synapse account",
                    onClick     = { showSignOutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section: Appearance ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 100)) + slideInVertically(tween(500, 100)) { 40 }
        ) {
            SettingsSection(title = "Appearance") {
                ActionSettingsRow(
                    icon        = Icons.Rounded.Settings,
                    iconTint    = ElectricPurple,
                    label       = "App Theme Preset",
                    description = "Selected: ${ThemeManager.currentTheme.displayName}",
                    onClick     = { showThemeDialog = true }
                )
                SettingsDivider()
                ActionSettingsRow(
                    icon        = Icons.Rounded.Info,
                    iconTint    = Color(0xFF06B6D4),
                    label       = "Key Sounds",
                    description = if (soundEnabled) "Preset: ${KeySoundEngine.activePreset.displayName}" else "Tap to configure",
                    onClick     = { onNavigateToKeySounds?.invoke() }
                )
                SettingsDivider()
                ToggleSettingsRow(
                    icon        = Icons.Rounded.Vibration,
                    iconTint    = ElectricPurple,
                    label       = "Vibration on Key Tap",
                    description = "Vibrate when you press a key.",
                    checked     = vibrationEnabled,
                    onCheckedChange = { enabled ->
                        vibrationEnabled = enabled
                        HapticEngine.setEnabled(prefs, enabled)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section: AI Engine Selection ──────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 120)) + slideInVertically(tween(500, 120)) { 40 }
        ) {
            SettingsSection(title = "AI Engine Selection") {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SynapseModel.values().forEach { model ->
                        val isSelected = model.key == selectedModelKey
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    brush = if (isSelected) Brush.linearGradient(listOf(ElectricPurple, Color(0xFF06B6D4)))
                                            else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.04f))),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(
                                    if (isSelected) ElectricPurple.copy(alpha = 0.10f)
                                    else Color(0xFF161622)
                                )
                                .clickable {
                                    prefs.edit().putString("synapse_selected_model", model.key).apply()
                                    selectedModelKey = model.key
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text       = model.displayName,
                                        color      = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 15.sp
                                    )
                                    if (model.isRecommended) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(EmeraldGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                .border(1.dp, EmeraldGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "RECOMMENDED",
                                                color = EmeraldGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text  = model.description,
                                    color = MutedGrey.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                            // Radio indicator
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElectricPurple else Color.Transparent)
                                    .border(2.dp, if (isSelected) ElectricPurple else MutedGrey.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section: Keyboard Management ──────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 140)) + slideInVertically(tween(500, 140)) { 40 }
        ) {
            SettingsSection(title = "Keyboard Management") {
                ActionSettingsRow(
                    icon        = Icons.Rounded.Settings,
                    iconTint    = ElectricPurple,
                    label       = "Test Keyboard",
                    description = "Open test notepad to try your keyboard",
                    onClick     = { onNavigateToKeyboardTest?.invoke() }
                )
                SettingsDivider()
                ActionSettingsRow(
                    icon        = Icons.Rounded.Settings,
                    iconTint    = Color(0xFF06B6D4),
                    label       = "Keyboard Settings",
                    description = "Manage keyboard preferences",
                    onClick     = {
                        try {
                            val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                )
                SettingsDivider()
                ActionSettingsRow(
                    icon        = Icons.Rounded.Refresh,
                    iconTint    = EmeraldGreen,
                    label       = "Switch Keyboard",
                    description = "Change default keyboard",
                    onClick     = {
                        try {
                            val imm = context.getSystemService(android.view.inputmethod.InputMethodManager::class.java)
                            imm?.showInputMethodPicker()
                        } catch (e: Exception) {
                            // Ignore
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section: Advanced ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 220)) + slideInVertically(tween(500, 220)) { 40 }
        ) {
            SettingsSection(title = "Advanced") {
                ActionSettingsRow(
                    icon        = Icons.Rounded.Delete,
                    iconTint    = Color(0xFFEF4444),
                    label       = "Clear AI Cache",
                    description = "Delete locally cached AI responses",
                    onClick     = { /* Clean action */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Section: Help & Support ───────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 260)) + slideInVertically(tween(500, 260)) { 40 }
        ) {
            SettingsSection(title = "Help & Support") {
                ActionSettingsRow(
                    icon        = Icons.Rounded.Info,
                    iconTint    = ElectricPurple,
                    label       = "How to Use",
                    description = "Re-open the onboarding tutorial",
                    onClick     = { /* Tutorial trigger */ }
                )
                SettingsDivider()
                ActionSettingsRow(
                    icon        = Icons.Rounded.Email,
                    iconTint    = Color(0xFF06B6D4),
                    label       = "Contact Support",
                    description = "Send us an email",
                    onClick     = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@synapsekeyboard.app")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }

        // ── Footer ────────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(500, 300))
        ) {
            Column(
                modifier             = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint               = MutedGrey.copy(alpha = 0.45f),
                        modifier           = Modifier.size(13.dp)
                    )
                    Text(
                        text       = "Synapse Keyboard",
                        fontWeight = FontWeight.Bold,
                        color      = MutedGrey.copy(alpha = 0.45f),
                        fontSize   = 13.sp
                    )
                }
                Text(
                    text     = "Version 1.0.0 (Alpha)",
                    color    = MutedGrey.copy(alpha = 0.30f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }

    // ── Sign Out Confirmation Dialog ──────────────────────────────────────────
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text       = "Sign Out?",
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            },
            text = {
                Text(
                    text     = "You'll need to sign in again to use Synapse AI features.",
                    color    = MutedGrey.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        scope.launch {
                            AuthManager.signOut()
                            onSignOut?.invoke()
                        }
                    }
                ) {
                    Text(
                        text       = "Sign Out",
                        color      = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(text = "Cancel", color = MutedGrey)
                }
            },
            containerColor = DeepSlate,
            shape          = RoundedCornerShape(22.dp),
            modifier       = Modifier.border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(22.dp))
        )
    }

    // ── App Theme Selector Dialog ─────────────────────────────────────────────
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    text       = "Select App Theme Preset",
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            },
            text = {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppThemePreset.values().forEach { preset ->
                        val isSelected = preset == ThemeManager.currentTheme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    brush = if (isSelected) Brush.linearGradient(listOf(preset.primary, preset.secondary))
                                            else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.04f))),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(
                                    if (isSelected) preset.primary.copy(alpha = 0.10f)
                                    else Color(0xFF161622)
                                )
                                .clickable {
                                    ThemeManager.selectTheme(context, preset)
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text       = preset.displayName,
                                    color      = Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize   = 14.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.primary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.background)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.secondary)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(preset.surface)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(text = "Done", color = ElectricPurple, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DeepSlate,
            shape          = RoundedCornerShape(22.dp),
            modifier       = Modifier.border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(22.dp))
        )
    }
}

// ---------------------------------------------------------------------------
// Reusable Settings Section wrapper with styled card layout
// ---------------------------------------------------------------------------
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
        Row(
            modifier          = Modifier.padding(start = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(11.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ElectricPurple)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text          = title.uppercase(),
                style         = MaterialTheme.typography.labelSmall,
                color         = MutedGrey.copy(alpha = 0.55f),
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = ElectricPurple.copy(alpha = 0.16f),
                    ambientColor = ElectricPurple.copy(alpha = 0.04f)
                )
                .clip(RoundedCornerShape(22.dp))
                .background(GlassmorphismColor)
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(Color.White.copy(0.08f), ElectricPurple.copy(0.12f), Color.White.copy(0.04f))
                    ),
                    RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Column(content = content)
        }
    }
}

// ---------------------------------------------------------------------------
// Action row
// ---------------------------------------------------------------------------
@Composable
private fun ActionSettingsRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = androidx.compose.material.ripple.rememberRipple(color = ElectricPurple.copy(0.15f)),
                onClick           = onClick
            )
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.radialGradient(listOf(iconTint.copy(alpha = 0.20f), iconTint.copy(alpha = 0.05f)))
                )
                .border(1.dp, iconTint.copy(alpha = 0.22f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = TextColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, color = MutedGrey.copy(alpha = 0.65f), fontSize = 12.sp)
        }
        Icon(
            imageVector        = Icons.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint               = MutedGrey.copy(alpha = 0.35f),
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Toggle row — same premium card styling as ActionSettingsRow, but with a
// Switch instead of a chevron. Used for on/off preferences (e.g. vibration).
// ---------------------------------------------------------------------------
@Composable
private fun ToggleSettingsRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    Brush.radialGradient(listOf(iconTint.copy(alpha = 0.20f), iconTint.copy(alpha = 0.05f)))
                )
                .border(1.dp, iconTint.copy(alpha = 0.22f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = TextColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(description, color = MutedGrey.copy(alpha = 0.65f), fontSize = 12.sp)
        }
        Switch(
            checked          = checked,
            onCheckedChange  = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor    = Color.White,
                checkedTrackColor    = ElectricPurple,
                uncheckedThumbColor  = MutedGrey,
                uncheckedTrackColor  = MutedGrey.copy(alpha = 0.18f)
            )
        )
    }
}

// ---------------------------------------------------------------------------
// Subtle divider
// ---------------------------------------------------------------------------
@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MutedGrey.copy(alpha = 0.06f))
    )
}
