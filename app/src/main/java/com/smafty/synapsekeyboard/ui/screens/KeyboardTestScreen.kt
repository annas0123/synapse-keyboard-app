package com.smafty.synapsekeyboard.ui.screens

import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.ui.keyboard.KeyboardTheme

// ── Premium Minimal design tokens ─────────────────────────────────────────────
private val KT_Bg           = Color(0xFF0A0A0F)
private val KT_Surface      = Color(0xFF141420)
private val KT_SurfaceHigh  = Color(0xFF1C1C2A)
private val KT_Border       = Color(0xFF2A2A3A)
private val KT_Violet       = Color(0xFF7C5CFC)
private val KT_TextPrimary  = Color(0xFFF0F0F5)
private val KT_TextSecond   = Color(0xFF8888A0)
private val KT_Success      = Color(0xFF34D399)
private val KT_Error        = Color(0xFFF87171)

/**
 * KeyboardTestScreen — Phase 3 redesign.
 * - #0A0A0F background
 * - #141420 notepad + card surfaces, #2A2A3A borders
 * - Violet slider, 2-circle theme selector
 * - Backend (SharedPreferences) fully preserved
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyboardTestScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    var testText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    var showSettingsPanel by remember { mutableStateOf(true) }

    val prefs = context.getSharedPreferences("synapse_prefs", Context.MODE_PRIVATE)
    var keyboardScale by remember {
        mutableFloatStateOf(prefs.getFloat("keyboard_height_scale", 1.0f))
    }
    // Phase 2 migration: use fromPrefs() to handle old legacy theme names
    var activeTheme by remember {
        val savedTheme = prefs.getString("keyboard_theme", KeyboardTheme.PREMIUM_BLACK.name)
        mutableStateOf(KeyboardTheme.fromPrefs(savedTheme))
    }

    val imm = context.getSystemService(InputMethodManager::class.java)

    val isEnabled = remember {
        try {
            imm?.enabledInputMethodList?.any {
                it.packageName == "com.smafty.synapsekeyboard"
            } ?: false
        } catch (e: Exception) { false }
    }
    val isDefault = remember {
        try {
            val defaultIme = Settings.Secure.getString(
                context.contentResolver, "default_input_method"
            )
            defaultIme?.contains("com.smafty.synapsekeyboard") ?: false
        } catch (e: Exception) {
            try {
                imm?.enabledInputMethodList?.any {
                    it.packageName == "com.smafty.synapsekeyboard"
                } ?: false
            } catch (ex: Exception) { false }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        softwareKeyboardController?.show()
    }
    BackHandler { testText = ""; onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KT_Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Header ─────────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { testText = ""; onBack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = KT_TextPrimary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "Keyboard Test",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = KT_TextPrimary
                )
                Text(
                    text     = "Test your Synapse keyboard",
                    fontSize = 13.sp,
                    color    = KT_TextSecond
                )
            }
            IconButton(onClick = { showSettingsPanel = !showSettingsPanel }) {
                Icon(
                    imageVector        = Icons.Rounded.Tune,
                    contentDescription = "Toggle Panel",
                    tint               = if (showSettingsPanel) KT_Violet else KT_TextSecond
                )
            }
            IconButton(onClick = {
                try {
                    val intent = android.content.Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) { }
            }) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = KT_Violet)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Collapsible Settings Panel ─────────────────────────────────────────
        AnimatedVisibility(
            visible  = showSettingsPanel,
            enter    = expandVertically() + fadeIn(),
            exit     = shrinkVertically() + fadeOut(),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // Keyboard Status Card
                SectionCard {
                    SectionLabel(text = "KEYBOARD STATUS")
                    Spacer(modifier = Modifier.height(12.dp))
                    StatusRow(label = "Enabled",        isActive = isEnabled,              activeText = "Yes",   inactiveText = "No")
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusRow(label = "Set as Default", isActive = isDefault,              activeText = "Yes",   inactiveText = "No")
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusRow(label = "Ready to Use",   isActive = isEnabled && isDefault, activeText = "Ready", inactiveText = "Not Ready")

                    if (!isEnabled || !isDefault) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFBBF24).copy(alpha = 0.10f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text     = if (!isEnabled) "Enable Synapse in keyboard settings first"
                                           else "Set Synapse as default keyboard",
                                fontSize = 12.sp,
                                color    = Color(0xFFFBBF24)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Keyboard Size Card
                SectionCard {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SectionLabel(text = "KEYBOARD SIZE")
                        Text(
                            text = when {
                                keyboardScale <= 0.88f -> "Small"
                                keyboardScale <= 1.05f -> "Medium"
                                keyboardScale <= 1.20f -> "Large"
                                else                   -> "Extra Large"
                            },
                            color      = KT_Violet,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("S", "M", "L", "XL").forEach {
                            Text(it, color = KT_TextSecond, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Slider(
                        value          = keyboardScale,
                        onValueChange  = { newScale ->
                            keyboardScale = newScale
                            prefs.edit().putFloat("keyboard_height_scale", newScale).apply()
                        },
                        valueRange = 0.80f..1.30f,
                        steps      = 4,
                        colors     = SliderDefaults.colors(
                            thumbColor         = KT_Violet,
                            activeTrackColor   = KT_Violet,
                            inactiveTrackColor = KT_Border
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Keyboard Theme Card — 2-circle selector
                SectionCard {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SectionLabel(text = "KEYBOARD THEME")
                        Text(
                            text       = activeTheme.displayName,
                            color      = KT_Violet,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        KeyboardTheme.entries.forEach { themeItem ->
                            val isSelected = themeItem == activeTheme
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable(
                                        indication        = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick           = {
                                            activeTheme = themeItem
                                            prefs.edit().putString("keyboard_theme", themeItem.name).apply()
                                        }
                                    )
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(themeItem.keyboardBg)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.5.dp,
                                            color = if (isSelected) KT_Violet else themeItem.borderColor,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        verticalArrangement   = Arrangement.SpaceBetween,
                                        horizontalAlignment   = Alignment.CenterHorizontally,
                                        modifier              = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text       = themeItem.displayName,
                                            color      = themeItem.keyText,
                                            fontSize   = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(3.dp)).background(themeItem.keyFaceDefault))
                                            Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(3.dp)).background(themeItem.keyFaceDark))
                                            Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(3.dp)).background(themeItem.accentGradientStart))
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("✓", color = KT_Violet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Notepad label ──────────────────────────────────────────────────────
        SectionLabel(text = "TEST NOTEPAD", modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))

        // ── Notepad TextField ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(KT_Surface)
                .border(0.5.dp, KT_Border, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            BasicTextField(
                value         = testText,
                onValueChange = { testText = it },
                modifier      = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color      = KT_TextPrimary,
                    fontSize   = 16.sp,
                    lineHeight = 26.sp
                ),
                cursorBrush = SolidColor(KT_Violet),
                decorationBox = { innerTextField ->
                    if (testText.isEmpty()) {
                        Text(
                            text       = "Tap here to start typing…\n\nYour Synapse keyboard should appear.\n\nTry AI features, switch between QWERTY and symbols, test shift and caps lock.\n\nThis text clears when you press back.",
                            color      = KT_TextSecond.copy(alpha = 0.50f),
                            fontSize   = 15.sp,
                            lineHeight = 26.sp
                        )
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Action Buttons ─────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick  = { testText = "" },
                modifier = Modifier.weight(1f).height(48.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = KT_TextSecond),
                border   = androidx.compose.foundation.BorderStroke(0.5.dp, KT_Border),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Clear", fontWeight = FontWeight.Medium, color = KT_TextSecond)
            }
            Button(
                onClick = {
                    try { imm?.showInputMethodPicker() } catch (e: Exception) { }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = KT_Violet),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Switch Keyboard", fontWeight = FontWeight.Medium, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── Reusable card surface ─────────────────────────────────────────────────────
@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KT_Surface)
            .border(0.5.dp, KT_Border, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        content = content
    )
}

// ── Section label (uppercase, muted, 13sp) ────────────────────────────────────
@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text          = text,
        color         = KT_TextSecond,
        fontSize      = 12.sp,
        fontWeight    = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        modifier      = modifier
    )
}

// ── Status Row ────────────────────────────────────────────────────────────────
@Composable
private fun StatusRow(
    label: String,
    isActive: Boolean,
    activeText: String,
    inactiveText: String
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = if (isActive) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
            contentDescription = null,
            tint               = if (isActive) KT_Success else KT_Error,
            modifier           = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, color = KT_TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text       = if (isActive) activeText else inactiveText,
            color      = if (isActive) KT_Success else KT_TextSecond,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
