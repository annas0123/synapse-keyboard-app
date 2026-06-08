package com.smafty.synapsekeyboard.ui.screens

import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.ui.keyboard.KeyboardTheme
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import com.smafty.synapsekeyboard.ui.theme.GlassmorphismColor
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import com.smafty.synapsekeyboard.ui.theme.TextColor

/**
 * KeyboardTestScreen — Premium redesign. All backend state/prefs preserved.
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
    var activeTheme by remember {
        val savedTheme = prefs.getString("keyboard_theme", KeyboardTheme.DARK_ELEGANCE.name)
        mutableStateOf(
            try { KeyboardTheme.valueOf(savedTheme ?: KeyboardTheme.DARK_ELEGANCE.name) }
            catch (e: Exception) { KeyboardTheme.DARK_ELEGANCE }
        )
    }

    val imm = context.getSystemService(InputMethodManager::class.java)
    val enabledInputMethods = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_INPUT_METHODS) ?: ""
    val defaultInputMethod  = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) ?: ""
    val isEnabled = enabledInputMethods.contains("com.smafty.synapsekeyboard")
    val isDefault = defaultInputMethod.contains("com.smafty.synapsekeyboard")

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        softwareKeyboardController?.show()
    }
    BackHandler { testText = ""; onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlate)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // ── Header ─────────────────────────────────────────────────────────────
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { testText = ""; onBack() }) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = TextColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(listOf(ElectricPurple, ElectricPurple.copy(0f)))
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "Keyboard Test",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextColor
                )
                Text(
                    text  = "Test your Synapse keyboard",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGrey.copy(alpha = 0.65f)
                )
            }
            IconButton(onClick = { showSettingsPanel = !showSettingsPanel }) {
                Icon(
                    imageVector        = Icons.Rounded.Tune,
                    contentDescription = "Toggle Panel",
                    tint               = if (showSettingsPanel) ElectricPurple else MutedGrey.copy(alpha = 0.5f)
                )
            }
            IconButton(onClick = {
                try {
                    val intent = android.content.Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) { }
            }) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = ElectricPurple)
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = ElectricPurple.copy(alpha = 0.16f),
                            ambientColor = ElectricPurple.copy(alpha = 0.04f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassmorphismColor)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(11.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(ElectricPurple)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text          = "KEYBOARD STATUS",
                                style         = MaterialTheme.typography.labelSmall,
                                color         = MutedGrey.copy(alpha = 0.55f),
                                fontWeight    = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        StatusRow(label = "Enabled",      isActive = isEnabled,              activeText = "Yes",   inactiveText = "No")
                        Spacer(modifier = Modifier.height(8.dp))
                        StatusRow(label = "Set as Default", isActive = isDefault,            activeText = "Yes",   inactiveText = "No")
                        Spacer(modifier = Modifier.height(8.dp))
                        StatusRow(label = "Ready to Use", isActive = isEnabled && isDefault, activeText = "Ready", inactiveText = "Not Ready")
                        if (!isEnabled || !isDefault) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.10f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text  = if (!isEnabled) "Enable Synapse in keyboard settings first"
                                            else "Set Synapse as default keyboard",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFF59E0B),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keyboard Size Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = ElectricPurple.copy(alpha = 0.16f),
                            ambientColor = ElectricPurple.copy(alpha = 0.04f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassmorphismColor)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(11.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(ElectricPurple)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text          = "KEYBOARD SIZE",
                                    style         = MaterialTheme.typography.labelSmall,
                                    color         = MutedGrey.copy(alpha = 0.55f),
                                    fontWeight    = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            }
                            Text(
                                text = when {
                                    keyboardScale <= 0.88f -> "Small"
                                    keyboardScale <= 1.05f -> "Medium"
                                    keyboardScale <= 1.20f -> "Large"
                                    else                   -> "Extra Large"
                                },
                                color      = ElectricPurple,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("S", "M", "L", "XL").forEach {
                                Text(it, color = MutedGrey.copy(alpha = 0.50f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Slider(
                            value       = keyboardScale,
                            onValueChange = { newScale ->
                                keyboardScale = newScale
                                prefs.edit().putFloat("keyboard_height_scale", newScale).apply()
                            },
                            valueRange = 0.80f..1.30f,
                            steps      = 4,
                            colors     = SliderDefaults.colors(
                                thumbColor        = ElectricPurple,
                                activeTrackColor  = ElectricPurple,
                                inactiveTrackColor = MutedGrey.copy(alpha = 0.15f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Keyboard Theme Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = ElectricPurple.copy(alpha = 0.16f),
                            ambientColor = ElectricPurple.copy(alpha = 0.04f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(GlassmorphismColor)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(11.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(ElectricPurple)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text          = "KEYBOARD THEME",
                                    style         = MaterialTheme.typography.labelSmall,
                                    color         = MutedGrey.copy(alpha = 0.55f),
                                    fontWeight    = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            }
                            Text(
                                text       = activeTheme.displayName,
                                color      = ElectricPurple,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier              = Modifier.fillMaxWidth()
                        ) {
                            items(KeyboardTheme.values().size) { idx ->
                                val themeItem  = KeyboardTheme.values()[idx]
                                val isSelected = themeItem == activeTheme
                                Box(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .height(72.dp)
                                        .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(14.dp),
                                            spotColor = if (isSelected) ElectricPurple.copy(0.5f) else Color.Transparent)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(themeItem.keyboardBg)
                                        .then(
                                            if (isSelected) Modifier.border(1.5.dp, ElectricPurple.copy(0.70f), RoundedCornerShape(14.dp))
                                            else Modifier.border(1.dp, Color.White.copy(0.10f), RoundedCornerShape(14.dp))
                                        )
                                        .clickable {
                                            activeTheme = themeItem
                                            prefs.edit().putString("keyboard_theme", themeItem.name).apply()
                                        }
                                        .padding(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text       = themeItem.displayName,
                                            color      = themeItem.keyText,
                                            fontSize   = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines   = 1
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment     = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(themeItem.keyFaceDefault)
                                                .then(if (themeItem.hasBorder) Modifier.border(0.5.dp, themeItem.borderColor, RoundedCornerShape(4.dp)) else Modifier))
                                            Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp)).background(themeItem.keyFaceDark)
                                                .then(if (themeItem.hasBorder) Modifier.border(0.5.dp, themeItem.borderColor, RoundedCornerShape(4.dp)) else Modifier))
                                            Box(modifier = Modifier.size(16.dp).clip(RoundedCornerShape(4.dp))
                                                .background(Brush.linearGradient(listOf(themeItem.accentGradientStart, themeItem.accentGradientEnd))))
                                        }
                                    }
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(11.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(EmeraldGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text          = "TEST NOTEPAD",
                style         = MaterialTheme.typography.labelSmall,
                color         = MutedGrey.copy(alpha = 0.55f),
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.1.sp
            )
        }

        // ── Notepad TextField ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = ElectricPurple.copy(alpha = 0.16f),
                    ambientColor = ElectricPurple.copy(alpha = 0.04f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(GlassmorphismColor)
                .padding(16.dp)
        ) {
            BasicTextField(
                value         = testText,
                onValueChange = { testText = it },
                modifier      = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color      = TextColor,
                    fontSize   = 16.sp,
                    lineHeight = 26.sp
                ),
                cursorBrush = SolidColor(ElectricPurple),
                decorationBox = { innerTextField ->
                    if (testText.isEmpty()) {
                        Text(
                            text       = "Tap here to start typing…\n\nYour Synapse keyboard should appear.\n\nTry AI features, switch between QWERTY and symbols, test shift and caps lock.\n\nThis text clears when you press back.",
                            color      = MutedGrey.copy(alpha = 0.40f),
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
            Button(
                onClick  = { testText = "" },
                modifier = Modifier.weight(1f).height(48.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = MutedGrey.copy(alpha = 0.18f)),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Text("Clear", fontWeight = FontWeight.SemiBold, color = TextColor)
            }
            Button(
                onClick = {
                    try { imm?.showInputMethodPicker() } catch (e: Exception) { }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape    = RoundedCornerShape(14.dp)
            ) {
                Text("Switch Keyboard", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
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
            tint               = if (isActive) EmeraldGreen else Color(0xFFEF4444),
            modifier           = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, color = TextColor, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text       = if (isActive) activeText else inactiveText,
            color      = if (isActive) EmeraldGreen else MutedGrey.copy(alpha = 0.55f),
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
