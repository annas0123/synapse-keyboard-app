package com.smafty.synapsekeyboard.ui.keyboard

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.smafty.synapsekeyboard.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import com.smafty.synapsekeyboard.ui.theme.CrispWhite
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.collectAsState
import com.smafty.synapsekeyboard.data.model.LanguageLayouts



// ---------------------------------------------------------------------------
// Symbol rows (page 1) — matches Gboard reference
// ---------------------------------------------------------------------------
private val SYM1_ROW_1 = listOf("1","2","3","4","5","6","7","8","9","0")
private val SYM1_ROW_2 = listOf("@","#","$","_","&","-","+","(",")","/")
private val SYM1_ROW_3 = listOf("*","\"","'",":",";","!","?")

// ---------------------------------------------------------------------------
// Symbol rows (page 2) — matches Gboard reference
// ---------------------------------------------------------------------------
private val SYM2_ROW_1 = listOf("~","\\","|","•","√","π","÷","×","§","Δ")
private val SYM2_ROW_2 = listOf("£","¢","€","¥","^","°","=","{","}","\\")
private val SYM2_ROW_3 = listOf("%","©","®","™","✓","[","]")

// ---------------------------------------------------------------------------
// Theme CompositionLocal definition
// ---------------------------------------------------------------------------
val LocalKeyboardTheme = staticCompositionLocalOf { KeyboardTheme.DARK_ELEGANCE }


// ---------------------------------------------------------------------------
// Root keyboard composable — called from the IME service
// ---------------------------------------------------------------------------
@Composable
fun SynapseKeyboardView(
    state: KeyboardUiState,
    onChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onReturn: () -> Unit,
    onSpace: () -> Unit,
    onAiAction: (String) -> Unit,
    onAcceptAi: () -> Unit,
    onRejectAi: () -> Unit,
    onCancelAi: () -> Unit,
    onSettingsClick: () -> Unit,
    onCopyItemToField: (String) -> Unit,
    onTogglePinClipboard: (String) -> Unit,
    onDeleteClipboardItem: (String) -> Unit,
    onClearClipboard: () -> Unit,
    onMoveCursor: (Int, Boolean) -> Unit,
    onDpadAction: (Int) -> Unit,
    onSaveVisibleTools: (List<String>) -> Unit,
    onSaveTheme: (KeyboardTheme) -> Unit,
    onSaveKeyScale: (Float) -> Unit
) {
    CompositionLocalProvider(LocalKeyboardTheme provides state.activeTheme) {
        val theme = LocalKeyboardTheme.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(theme.keyboardBg)
        ) {
            // Top toolbar — AI buttons OR Accept/Reject (hidden in emoji mode)
            if (state.mode != KeyboardMode.EMOJI) {
                AnimatedContent(
                    targetState = state.aiOutputState,
                    transitionSpec = {
                        (fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.92f))
                            .togetherWith(fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.92f))
                    },
                    label = "toolbarTransition"
                ) { aiState ->
                    when (aiState) {
                        AiOutputState.SHOWING_RESULT -> AcceptRejectToolbar(
                            suggestion      = state.aiSuggestion,
                            inputEnergy     = state.lastInputEnergy,
                            outputEnergy    = state.lastOutputEnergy,
                            energyRemaining = state.energyRemaining,
                            onAccept = onAcceptAi,
                            onReject = onRejectAi
                        )
                        else -> AiToolbar(
                            state = state,
                            isLoading = aiState == AiOutputState.LOADING,
                            onSettingsClick = onSettingsClick,
                            onCancelAi = onCancelAi,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Key rows
            when (state.mode) {
                KeyboardMode.QWERTY -> QwertyLayout(state, onChar, onBackspace, onReturn, onSpace)
                KeyboardMode.SYMBOLS_1 -> SymbolsLayout(
                    row1 = SYM1_ROW_1, row2 = SYM1_ROW_2, row3 = SYM1_ROW_3,
                    isPage2 = false,
                    state = state, onChar = onChar, onBackspace = onBackspace,
                    onReturn = onReturn, onSpace = onSpace
                )
                KeyboardMode.SYMBOLS_2 -> SymbolsLayout(
                    row1 = SYM2_ROW_1, row2 = SYM2_ROW_2, row3 = SYM2_ROW_3,
                    isPage2 = true,
                    state = state, onChar = onChar, onBackspace = onBackspace,
                    onReturn = onReturn, onSpace = onSpace
                )
                KeyboardMode.EMOJI -> EmojiLayout(
                    state = state,
                    onEmojiClick = onChar,
                    onBackspace = onBackspace
                )
                KeyboardMode.CLIPBOARD -> ClipboardHistoryPanel(
                    state = state,
                    onItemClick = onCopyItemToField,
                    onTogglePin = onTogglePinClipboard,
                    onDeleteItem = onDeleteClipboardItem,
                    onClearAll = onClearClipboard
                )
                KeyboardMode.TEXT_NAV -> TextNavPanel(
                    state = state,
                    onMoveCursor = onMoveCursor,
                    onDpadAction = onDpadAction
                )
                KeyboardMode.AI_PROMPTS -> AiPromptsPanel(
                    state = state,
                    onPromptExecute = { promptInstruction ->
                        state.selectedPromptInstruction = ""
                        state.exitSpecialMode()
                        onAiAction(promptInstruction)
                    }
                )
                KeyboardMode.TOOL_MANAGER -> ToolManagerPanel(
                    state = state,
                    onSaveAndExit = { updatedTools ->
                        onSaveVisibleTools(updatedTools)
                        state.mode = KeyboardMode.QWERTY
                    }
                )
                KeyboardMode.THEME_SWITCHER -> MiniThemeSwitcherPanel(
                    state = state,
                    onThemeSelected = { theme ->
                        onSaveTheme(theme)
                        state.exitSpecialMode()
                    }
                )
                KeyboardMode.SIZE_PANEL -> SizeSelectorPanel(
                    state = state,
                    onSizeSelected = { scale ->
                        onSaveKeyScale(scale)
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// AI Toolbar — premium 3-part layout: [Logo] [Centered Tools] [Grid Menu]
// ---------------------------------------------------------------------------
@Composable
private fun AiToolbar(
    state: KeyboardUiState,
    isLoading: Boolean,
    onSettingsClick: () -> Unit,
    onCancelAi: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    // Gold accent for the AI logo ring (works across all themes)
    val goldColor = Color(0xFFD97706)
    val goldBrush  = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))

    val infiniteTransition = rememberInfiniteTransition(label = "synapse_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(theme.toolbarBg)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── FAR LEFT: S section (Custom Prompts / AI presets) ─────────────────
        val isAiActive = state.mode == KeyboardMode.AI_PROMPTS
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(
                    if (isAiActive) theme.accentGradientStart.copy(alpha = 0.20f)
                    else Color.Transparent
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        if (state.mode == KeyboardMode.AI_PROMPTS) state.exitSpecialMode()
                        else state.switchToAiPrompts()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_synapse),
                contentDescription = "AI Prompt Presets",
                tint = if (isAiActive) theme.accentGradientStart else theme.keyText,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ── CENTER: AI status OR tool icons ──────────────────────────────────
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.aiProcessingState == AiProcessingState.PROCESSING -> {
                    // Processing overlay
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_synapse),
                            contentDescription = "AI Thinking",
                            tint = theme.accentGradientStart,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp)
                                .rotate(rotationAngle)
                        )
                        if (state.aiProcessingBuffer.isNotEmpty()) {
                            Text(
                                text = state.aiProcessingBuffer,
                                color = theme.accentGradientEnd,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .padding(end = 6.dp)
                            )
                        } else {
                            Text(
                                text = "AI Thinking… ✨",
                                color = theme.accentGradientStart,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        }
                        // Cancel button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.18f))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = onCancelAi
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                state.aiProcessingState == AiProcessingState.ERROR -> {
                    Text(
                        text = state.aiErrorMessage.ifEmpty { "AI error. Try again." },
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                isLoading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_synapse),
                            contentDescription = "AI Thinking",
                            tint = theme.accentGradientStart,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp)
                                .rotate(rotationAngle)
                        )
                        Text(
                            text = "Thinking… ✨",
                            color = theme.accentGradientStart,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                else -> {
                    // ── Centered customisable tool icons + permanent keyboard icon ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ── FIRST: Keyboard icon ──────────────────────────────────
                        val isKeyboardActive = state.mode == KeyboardMode.QWERTY ||
                            state.mode == KeyboardMode.SYMBOLS_1 ||
                            state.mode == KeyboardMode.SYMBOLS_2
                        ToolbarIconButton(
                            icon = Icons.Rounded.Keyboard,
                            description = "Keyboard",
                            isActive = isKeyboardActive,
                            theme = theme,
                            onClick = { state.mode = KeyboardMode.QWERTY }
                        )

                        // ── Dynamic tools from visibleTools (excluding AI_PROMPTS since it's pinned to the far left) ──
                        state.visibleTools.forEach { toolId ->
                            when (toolId) {
                                TOOL_CLIPBOARD -> ToolbarIconButton(
                                    icon = Icons.Rounded.ContentPaste,
                                    description = "Clipboard History",
                                    isActive = state.mode == KeyboardMode.CLIPBOARD,
                                    theme = theme,
                                    onClick = {
                                        if (state.mode == KeyboardMode.CLIPBOARD) state.exitSpecialMode()
                                        else state.switchToClipboard()
                                    }
                                )
                                TOOL_TEXT_NAV -> ToolbarIconButton(
                                    icon = Icons.Rounded.TextFormat,
                                    description = "Text Navigation",
                                    isActive = state.mode == KeyboardMode.TEXT_NAV,
                                    theme = theme,
                                    onClick = {
                                        if (state.mode == KeyboardMode.TEXT_NAV) state.exitSpecialMode()
                                        else state.switchToTextNav()
                                    }
                                )
                                TOOL_AI_PROMPTS -> { /* Pinned to far left — skip */ }
                                TOOL_THEME_SWITCHER -> ToolbarIconButton(
                                    icon = Icons.Rounded.Palette,
                                    description = "Mini Theme Switcher",
                                    isActive = state.mode == KeyboardMode.THEME_SWITCHER,
                                    theme = theme,
                                    onClick = {
                                        if (state.mode == KeyboardMode.THEME_SWITCHER) state.exitSpecialMode()
                                        else state.switchToThemeSwitcher()
                                    }
                                )
                                TOOL_SIZE_PANEL -> ToolbarIconButton(
                                    icon = Icons.Rounded.AspectRatio,
                                    description = "Keyboard Size",
                                    isActive = state.mode == KeyboardMode.SIZE_PANEL,
                                    theme = theme,
                                    onClick = {
                                        if (state.mode == KeyboardMode.SIZE_PANEL) state.exitSpecialMode()
                                        else state.switchToSizePanel()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ── FAR RIGHT: Grid / Tool Manager button ────────────────────────────
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(
                    if (state.mode == KeyboardMode.TOOL_MANAGER)
                        theme.accentGradientStart.copy(alpha = 0.20f)
                    else
                        Color.Transparent
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        if (state.mode == KeyboardMode.TOOL_MANAGER) state.exitSpecialMode()
                        else state.switchToToolManager()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.GridView,
                contentDescription = "Customize Toolbar",
                tint = if (state.mode == KeyboardMode.TOOL_MANAGER)
                    theme.accentGradientStart
                else
                    theme.keyTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Small reusable icon button for the centered toolbar section
@Composable
private fun ToolbarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    isActive: Boolean,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (isActive) theme.accentGradientStart else theme.keyTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ToolbarIconButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    description: String,
    isActive: Boolean,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = description,
            tint = if (isActive) theme.accentGradientStart else theme.keyTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Accept / Reject toolbar — shown when AI has output
// ---------------------------------------------------------------------------
@Composable
private fun AcceptRejectToolbar(
    suggestion: String,
    inputEnergy: Int,
    outputEnergy: Int,
    energyRemaining: Int,
    onAccept: () -> Unit,
    onReject: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current

    // Dynamic color based on remaining energy quota (Blueprint 26 A-3)
    val badgeColor = when {
        energyRemaining <= 0   -> Color(0xFFEF4444)  // 🔴 Out of energy
        energyRemaining < 500  -> Color(0xFFF59E0B)  // 🟡 Warning threshold
        else                  -> EmeraldGreen        // 🟢 Healthy
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(theme.toolbarBg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Suggestion preview text
        Text(
            text = if (suggestion.length > 40) suggestion.take(37) + "…" else suggestion,
            color = theme.keyTextMuted,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        // Energy cost badge: "In: 14 | Out: 48" — only shown when there was real API energy consumed
        if (inputEnergy > 0 || outputEnergy > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor.copy(alpha = 0.14f))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "In: ${inputEnergy} | Out: ${outputEnergy}",
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        ActionButton(
            icon = Icons.Rounded.Close,
            label = "Reject",
            color = Color(0xFFEF4444),
            onClick = onReject
        )
        ActionButton(
            icon = Icons.Rounded.Check,
            label = "Accept",
            color = theme.accentGradientEnd,
            onClick = onAccept
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.18f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ---------------------------------------------------------------------------
// QWERTY layout — with long-press number hints
// ---------------------------------------------------------------------------
@Composable
private fun QwertyLayout(
    state: KeyboardUiState,
    onChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onReturn: () -> Unit,
    onSpace: () -> Unit,
) {
    val keyHeight = (44 * state.keyHeightScale).dp
    val layout    = LanguageLayouts.getLayout(state.activeLanguage)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1 — with number long-press hints
        KeyRow {
            layout.row1.forEachIndexed { idx, ch ->
                val hint = layout.row1Hints.getOrElse(idx) { "" }
                CharKeyWithHint(
                    label = if (layout.supportsShift && state.isUpperCase()) ch.uppercase() else ch,
                    hint  = hint,
                    weight = 1f,
                    height = keyHeight,
                    onPress = {
                        onChar(if (layout.supportsShift && state.isUpperCase()) ch.uppercase() else ch)
                        state.consumeShiftAfterKey()
                    },
                    onLongPress = { if (hint.isNotEmpty()) onChar(hint) }
                )
            }
        }

        // Row 2 — slightly inset
        KeyRow(horizontalPadding = 16.dp) {
            layout.row2.forEach { ch ->
                CharKey(
                    label = if (layout.supportsShift && state.isUpperCase()) ch.uppercase() else ch,
                    weight = 1f,
                    height = keyHeight,
                    onPress = {
                        onChar(if (layout.supportsShift && state.isUpperCase()) ch.uppercase() else ch)
                        state.consumeShiftAfterKey()
                    }
                )
            }
        }

        // Row 3 — ⇧ [chars] ⌫
        KeyRow {
            if (layout.supportsShift) {
                ShiftKey(
                    isActive  = state.isShiftActive,
                    isCapsLock = state.isCapsLock,
                    height    = keyHeight,
                    onPress   = { state.onShiftTap() }
                )
                Spacer(Modifier.width(4.dp))
            }

            layout.row3.forEach { ch ->
                CharKey(
                    label = if (layout.supportsShift && state.isUpperCase()) ch.uppercase() else ch,
                    weight = 1f,
                    height = keyHeight,
                    onPress = {
                        onChar(if (layout.supportsShift && state.isUpperCase()) ch.uppercase() else ch)
                        state.consumeShiftAfterKey()
                    }
                )
            }

            Spacer(Modifier.width(4.dp))
            BackspaceKey(height = keyHeight, onPress = onBackspace)
        }

        // Bottom row — ?123 , 🌐/😊 [space] . ↵
        BottomRow(
            state         = state,
            mode          = state.mode,
            height        = keyHeight,
            onChar        = onChar,
            onModeToggle  = {
                state.mode = if (state.mode == KeyboardMode.QWERTY)
                    KeyboardMode.SYMBOLS_1 else KeyboardMode.QWERTY
            },
            onEmojiToggle  = { state.switchToEmoji() },
            onSpace       = onSpace,
            onReturn      = onReturn
        )
    }
}

// ---------------------------------------------------------------------------
// Symbols layout (shared for both pages)
// ---------------------------------------------------------------------------
@Composable
private fun SymbolsLayout(
    row1: List<String>,
    row2: List<String>,
    row3: List<String>,
    isPage2: Boolean,
    state: KeyboardUiState,
    onChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onReturn: () -> Unit,
    onSpace: () -> Unit,
) {
    val keyHeight = (44 * state.keyHeightScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        KeyRow { row1.forEach { ch -> CharKey(ch, 1f, keyHeight) { onChar(ch) } } }
        KeyRow { row2.forEach { ch -> CharKey(ch, 1f, keyHeight) { onChar(ch) } } }

        KeyRow {
            // Toggle page 1 ↔ page 2
            FuncKey(
                label = if (isPage2) "?123" else "=\\<",
                weight = 1.5f,
                height = keyHeight,
                onPress = {
                    state.mode = if (isPage2) KeyboardMode.SYMBOLS_1 else KeyboardMode.SYMBOLS_2
                }
            )
            Spacer(Modifier.width(4.dp))
            row3.forEach { ch -> CharKey(ch, 1f, keyHeight) { onChar(ch) } }
            Spacer(Modifier.width(4.dp))
            BackspaceKey(height = keyHeight, onPress = onBackspace)
        }

        BottomRow(
            state           = state,
            mode            = state.mode,
            height          = keyHeight,
            onChar          = onChar,
            onModeToggle    = { state.mode = KeyboardMode.QWERTY },
            onEmojiToggle   = { state.switchToEmoji() },
            onSpace         = onSpace,
            onReturn        = onReturn
        )
    }
}

// ---------------------------------------------------------------------------
// Emoji layout — grid + category tabs
// ---------------------------------------------------------------------------
@Composable
private fun EmojiLayout(
    state: KeyboardUiState,
    onEmojiClick: (String) -> Unit,
    onBackspace: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    val categories = EmojiProvider.allCategories
    val selectedCategory = categories.getOrElse(state.emojiCategoryIndex) { EmojiCategory.SMILEYS }
    val emojis = EmojiProvider.getEmojis(selectedCategory)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Category tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(theme.toolbarBg)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categories.forEachIndexed { idx, cat ->
                val isSelected = idx == state.emojiCategoryIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) theme.accentGradientStart.copy(alpha = 0.25f)
                            else Color.Transparent
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { state.emojiCategoryIndex = idx }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = EmojiProvider.categoryIcons[cat] ?: "😀",
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Emoji grid or empty-state
        val displayEmojis = EmojiProvider.getEmojis(selectedCategory)
        if (displayEmojis.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🕐", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No recent emojis yet",
                        color = theme.keyTextMuted.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Tap any emoji to save it here",
                        color = theme.keyTextMuted.copy(alpha = 0.4f),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(displayEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = { onEmojiClick(emoji) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 24.sp)
                    }
                }
            }
        }

        // Bottom bar: ABC | backspace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ABC — go back to letters
            FuncKey(
                label = "ABC",
                weight = 1.5f,
                height = 40.dp,
                onPress = { state.exitEmoji() }
            )

            // Space bar
            Box(
                modifier = Modifier
                    .weight(4.5f)
                    .height(40.dp)
                    .keyStyle(theme, false)
                    .pointerInput(Unit) { detectTapGestures(onTap = { onEmojiClick(" ") }) },
                contentAlignment = Alignment.Center
            ) {
                Text("English", color = theme.keyTextMuted, fontSize = 12.sp)
            }

            // Backspace
            BackspaceKey(weight = 1.5f, height = 40.dp, onPress = onBackspace)
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom row: mode-switch | , | 😊 | space | . | return
// ---------------------------------------------------------------------------
@Composable
private fun BottomRow(
    state: KeyboardUiState,
    mode: KeyboardMode,
    height: Dp,
    onChar: (String) -> Unit,
    onModeToggle: () -> Unit,
    onEmojiToggle: () -> Unit,
    onSpace: () -> Unit,
    onReturn: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode toggle (?123 / ABC)
        FuncKey(
            label = if (mode == KeyboardMode.QWERTY) "?123" else "ABC",
            weight = 1.5f,
            height = height,
            onPress = onModeToggle
        )

        // Comma key
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height)
                .pointerInput(Unit) { detectTapGestures(onTap = { onChar(",") }) }
                .padding(horizontal = KeyHorizontalPadding)
                .keyStyle(theme, true),
            contentAlignment = Alignment.Center
        ) {
            Text(",", color = theme.keyTextMuted, fontSize = 16.sp)
        }

        // Emoji toggle (language cycle removed — English-only keyboard)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onEmojiToggle
                )
                .padding(horizontal = KeyHorizontalPadding)
                .keyStyle(theme, true),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_emoji),
                contentDescription = "Emoji",
                tint = theme.keyTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        // Space bar — shows active language name
        Box(
            modifier = Modifier
                .weight(4f)
                .height(height)
                .pointerInput(Unit) { detectTapGestures(onTap = { onSpace() }) }
                .padding(horizontal = KeyHorizontalPadding)
                .keyStyle(theme, false),
            contentAlignment = Alignment.Center
        ) {
            Text(state.activeLanguage, color = theme.keyTextMuted, fontSize = 12.sp)
        }

        // Period key
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height)
                .pointerInput(Unit) { detectTapGestures(onTap = { onChar(".") }) }
                .padding(horizontal = KeyHorizontalPadding)
                .keyStyle(theme, true),
            contentAlignment = Alignment.Center
        ) {
            Text(".", color = theme.keyTextMuted, fontSize = 16.sp)
        }

        // Return / Enter key
        val returnBrush = Brush.linearGradient(
            listOf(theme.accentGradientStart.copy(0.85f), theme.accentGradientEnd.copy(0.85f))
        )
        Box(
            modifier = Modifier
                .weight(1.5f)
                .height(height)
                .pointerInput(Unit) { detectTapGestures(onTap = { onReturn() }) }
                .padding(horizontal = KeyHorizontalPadding)
                .clip(KeyShape)
                .then(
                    if (theme.hasBorder) Modifier.border(1.dp, theme.borderColor, KeyShape) else Modifier
                )
                .background(returnBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.KeyboardReturn,
                contentDescription = "Return",
                tint = CrispWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable key primitives
// ---------------------------------------------------------------------------
private val KeyShape = RoundedCornerShape(7.dp)
private val KeyHorizontalPadding = 2.5.dp
private val KeyTouchSlop = 10.dp
private const val KEY_PREVIEW_MIN_VISIBLE_MS = 70L

// ---------------------------------------------------------------------------
// Gboard-style key preview card (rendered inside a Popup above the key)
// ---------------------------------------------------------------------------
@Composable
private fun KeyPreviewCard(label: String, theme: KeyboardTheme) {
    val previewShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 46.dp, minHeight = 54.dp)
            .clip(previewShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        theme.keyFaceDefault.copy(alpha = 1f),
                        theme.keyFaceDark.copy(alpha = 0.97f)
                    )
                )
            )
            .border(
                1.dp,
                if (theme.hasBorder) theme.borderColor.copy(alpha = 0.9f)
                else Color.White.copy(alpha = 0.22f),
                previewShape
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = theme.keyText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Modifier.keyStyle(theme: KeyboardTheme, isDarkKey: Boolean): Modifier {
    val bg = if (isDarkKey) theme.keyFaceDark else theme.keyFaceDefault
    return this
        .clip(KeyShape)
        .then(
            if (theme.hasBorder) {
                Modifier.border(1.dp, theme.borderColor, KeyShape)
            } else {
                Modifier
            }
        )
        .background(bg)
}

@Composable
private fun KeyRow(
    horizontalPadding: Dp = 0.dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun RowScope.CharKey(
    label: String,
    weight: Float = 1f,
    height: Dp = 44.dp,
    onPress: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val touchSlopPx = with(density) { KeyTouchSlop.toPx() }
    var isPressed by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var previewToken by remember { mutableStateOf(0) }

    val keyScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "keyScale"
    )
    val pressedBg by animateColorAsState(
        targetValue = if (isPressed) theme.accentGradientStart.copy(alpha = 0.20f) else Color.Transparent,
        animationSpec = tween(40),
        label = "keyBg"
    )

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .pointerInput(Unit) {
                awaitEachGesture {
                    var activePreviewToken = 0
                    var keepPreviewAfterGesture = false
                    try {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        activePreviewToken = previewToken + 1
                        previewToken = activePreviewToken
                        isPressed = true
                        showPreview = true
                        keepPreviewAfterGesture = true
                        
                        val pointerId = down.id
                        var cancelled = false
                        
                        while (true) {
                            val event = awaitPointerEvent()
                            val pointerChange = event.changes.firstOrNull { it.id == pointerId }
                            
                            if (pointerChange == null) {
                                keepPreviewAfterGesture = false
                                break
                            }
                            val isUp = pointerChange.changedToUp()
                            
                            // Check if finger has dragged outside the key bounds
                            val x = pointerChange.position.x
                            val y = pointerChange.position.y
                            val isInside = x >= -touchSlopPx &&
                                x <= size.width.toFloat() + touchSlopPx &&
                                y >= -touchSlopPx &&
                                y <= size.height.toFloat() + touchSlopPx
                            
                            if (!isInside) {
                                isPressed = false
                                cancelled = true
                                keepPreviewAfterGesture = false
                                showPreview = false
                            } else if (!cancelled) {
                                isPressed = true
                            }
                            
                            if (isUp) {
                                if (isInside && !cancelled) {
                                    onPress()
                                }
                                break
                            }
                        }
                    } finally {
                        isPressed = false
                        if (keepPreviewAfterGesture) {
                            val tokenAtRelease = activePreviewToken
                            scope.launch {
                                delay(KEY_PREVIEW_MIN_VISIBLE_MS)
                                if (previewToken == tokenAtRelease) {
                                    showPreview = false
                                }
                            }
                        } else if (previewToken == activePreviewToken) {
                            showPreview = false
                        }
                    }
                }
            }
            .graphicsLayer { scaleX = keyScale; scaleY = keyScale }
            .padding(horizontal = KeyHorizontalPadding)
            .keyStyle(theme, false)
            .background(color = pressedBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = theme.keyText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        // Preview popup above the key
        if (showPreview) {
            val offsetY = with(density) { -(height + 6.dp).roundToPx() }
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = offsetY),
                properties = PopupProperties(focusable = false, clippingEnabled = false)
            ) {
                KeyPreviewCard(label = label.uppercase(), theme = theme)
            }
        }
    }
}

// Character key with a small hint label (e.g., number) in the top-right corner
@Composable
private fun RowScope.CharKeyWithHint(
    label: String,
    hint: String,
    weight: Float = 1f,
    height: Dp = 44.dp,
    onPress: () -> Unit,
    onLongPress: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val touchSlopPx = with(density) { KeyTouchSlop.toPx() }
    var isPressed by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }
    var previewToken by remember { mutableStateOf(0) }

    val keyScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "keyHintScale"
    )
    val pressedBg by animateColorAsState(
        targetValue = if (isPressed) theme.accentGradientStart.copy(alpha = 0.20f) else Color.Transparent,
        animationSpec = tween(40),
        label = "keyHintBg"
    )

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .pointerInput(Unit) {
                awaitEachGesture {
                    var activePreviewToken = 0
                    var keepPreviewAfterGesture = false
                    try {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        activePreviewToken = previewToken + 1
                        previewToken = activePreviewToken
                        isPressed = true
                        showPreview = true
                        keepPreviewAfterGesture = true
                        
                        val pointerId = down.id
                        var cancelled = false
                        var longPressed = false
                        
                        val job = scope.launch {
                            delay(400L)
                            if (!cancelled) {
                                longPressed = true
                                onLongPress()
                            }
                        }
                        
                        while (true) {
                            val event = awaitPointerEvent()
                            val pointerChange = event.changes.firstOrNull { it.id == pointerId }
                            
                            if (pointerChange == null) {
                                keepPreviewAfterGesture = false
                                job.cancel()
                                break
                            }
                            val isUp = pointerChange.changedToUp()
                            
                            val x = pointerChange.position.x
                            val y = pointerChange.position.y
                            val isInside = x >= -touchSlopPx &&
                                x <= size.width.toFloat() + touchSlopPx &&
                                y >= -touchSlopPx &&
                                y <= size.height.toFloat() + touchSlopPx
                            
                            if (!isInside) {
                                isPressed = false
                                cancelled = true
                                keepPreviewAfterGesture = false
                                showPreview = false
                                job.cancel()
                            } else if (!cancelled) {
                                isPressed = true
                            }
                            
                            if (isUp) {
                                job.cancel()
                                if (isInside && !cancelled && !longPressed) {
                                    onPress()
                                }
                                break
                            }
                        }
                    } finally {
                        isPressed = false
                        if (keepPreviewAfterGesture) {
                            val tokenAtRelease = activePreviewToken
                            scope.launch {
                                delay(KEY_PREVIEW_MIN_VISIBLE_MS)
                                if (previewToken == tokenAtRelease) {
                                    showPreview = false
                                }
                            }
                        } else if (previewToken == activePreviewToken) {
                            showPreview = false
                        }
                    }
                }
            }
            .graphicsLayer { scaleX = keyScale; scaleY = keyScale }
            .padding(horizontal = KeyHorizontalPadding)
            .keyStyle(theme, false)
            .background(color = pressedBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = hint,
            color = theme.keyTextMuted.copy(alpha = 0.5f),
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 4.dp, top = 2.dp)
        )
        Text(
            text = label,
            color = theme.keyText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (showPreview) {
            val offsetY = with(density) { -(height + 6.dp).roundToPx() }
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(x = 0, y = offsetY),
                properties = PopupProperties(focusable = false, clippingEnabled = false)
            ) {
                KeyPreviewCard(label = label.uppercase(), theme = theme)
            }
        }
    }
}

@Composable
private fun RowScope.ShiftKey(
    isActive: Boolean,
    isCapsLock: Boolean,
    weight: Float = 1.5f,
    height: Dp = 44.dp,
    onPress: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    val tint = when {
        isCapsLock -> theme.accentGradientEnd
        isActive   -> theme.accentGradientStart
        else       -> theme.keyTextMuted
    }
    val activeBg by animateColorAsState(
        targetValue = when {
            isCapsLock -> theme.accentGradientEnd.copy(alpha = 0.20f)
            isActive   -> theme.accentGradientStart.copy(alpha = 0.15f)
            else       -> Color.Transparent
        },
        animationSpec = tween(150),
        label = "shiftBg"
    )

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .pointerInput(Unit) { detectTapGestures(onTap = { onPress() }) }
            .padding(horizontal = KeyHorizontalPadding)
            .keyStyle(theme, true)
            .background(activeBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (isCapsLock) Icons.Rounded.KeyboardCapslock else Icons.Rounded.KeyboardArrowUp,
            contentDescription = if (isCapsLock) "Caps Lock" else "Shift",
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun RowScope.BackspaceKey(
    weight: Float = 1.5f,
    height: Dp = 44.dp,
    onPress: () -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val scope = rememberCoroutineScope()
    var isDepressed by remember { mutableStateOf(false) }

    val keyScale by animateFloatAsState(
        targetValue = if (isDepressed) 0.93f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bsScale"
    )
    val pressedBg by animateColorAsState(
        targetValue = if (isDepressed) theme.keyTextMuted.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(60),
        label = "bsBg"
    )

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isDepressed = true
                    onPress()
                    val job = scope.launch {
                        delay(400L)
                        var repeatCount = 0
                        while (true) {
                            onPress()
                            repeatCount++
                            delay(if (repeatCount <= 5) 80L else 40L)
                        }
                    }
                    waitForUpOrCancellation()
                    isDepressed = false
                    job.cancel()
                }
            }
            .graphicsLayer { scaleX = keyScale; scaleY = keyScale }
            .padding(horizontal = KeyHorizontalPadding)
            .keyStyle(theme, true)
            .background(color = pressedBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Backspace,
            contentDescription = "Backspace",
            tint = theme.keyTextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun RowScope.FuncKey(
    label: String,
    weight: Float = 1.5f,
    height: Dp = 44.dp,
    onPress: () -> Unit,
) {
    val theme = LocalKeyboardTheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val keyScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "funcScale"
    )
    val pressedBg by animateColorAsState(
        targetValue = if (isPressed) theme.keyTextMuted.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(60),
        label = "funcBg"
    )

    Box(
        modifier = Modifier
            .weight(weight)
            .height(height)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onPress
            )
            .graphicsLayer { scaleX = keyScale; scaleY = keyScale }
            .padding(horizontal = KeyHorizontalPadding)
            .keyStyle(theme, true)
            .background(color = pressedBg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = theme.keyTextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// ---------------------------------------------------------------------------
// Reusable Panel Components
// ---------------------------------------------------------------------------
@Composable
private fun PanelHeader(
    title: String,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    val theme = LocalKeyboardTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(theme.toolbarBg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = theme.keyText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (actions != null) {
            actions()
        }
    }
}

// ---------------------------------------------------------------------------
// 1. Clipboard History Panel
// ---------------------------------------------------------------------------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClipboardHistoryPanel(
    state: KeyboardUiState,
    onItemClick: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val panelHeight = (220 * state.keyHeightScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(theme.keyboardBg)
    ) {
        PanelHeader(
            title = "Clipboard History",
            actions = {
                if (state.clipboardItems.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = "Clear unpinned history",
                        tint = if (theme.isDark) Color(0xFFEF4444) else Color(0xFFDC2626),
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onClearAll
                            )
                            .padding(2.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (state.clipboardItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentPasteSearch,
                    contentDescription = "Empty clipboard",
                    tint = theme.keyTextMuted.copy(alpha = 0.4f),
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clipboard is empty",
                    color = theme.keyTextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Copied text will show up here",
                    color = theme.keyTextMuted.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(state.clipboardItems, key = { it.id }) { item ->
                    ClipboardCard(
                        item = item,
                        theme = theme,
                        onItemClick = onItemClick,
                        onTogglePin = onTogglePin,
                        onDeleteItem = onDeleteItem
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClipboardCard(
    item: ClipboardItem,
    theme: KeyboardTheme,
    onItemClick: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val cardBorder = if (item.isPinned) {
        Modifier.border(1.dp, theme.accentGradientStart.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
    } else if (theme.hasBorder) {
        Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    } else {
        Modifier
    }

    val cardBg = if (item.isPinned) {
        theme.keyFaceDefault.copy(alpha = 0.85f)
    } else {
        theme.keyFaceDefault.copy(alpha = 0.6f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .then(cardBorder)
            .combinedClickable(
                onClick = { onItemClick(item.text) },
                onLongClick = { onTogglePin(item.id) }
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.text,
            color = theme.keyText,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Pin Button
        Icon(
            imageVector = Icons.Rounded.PushPin,
            contentDescription = "Pin item",
            tint = if (item.isPinned) theme.accentGradientStart else theme.keyTextMuted.copy(alpha = 0.4f),
            modifier = Modifier
                .size(20.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onTogglePin(item.id) }
                )
                .padding(2.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Delete Button
        Icon(
            imageVector = Icons.Rounded.DeleteOutline,
            contentDescription = "Delete item",
            tint = theme.keyTextMuted.copy(alpha = 0.5f),
            modifier = Modifier
                .size(20.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onDeleteItem(item.id) }
                )
                .padding(2.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// 2. Cursor Navigation D-Pad Panel
// ---------------------------------------------------------------------------
@Composable
fun TextNavPanel(
    state: KeyboardUiState,
    onMoveCursor: (Int, Boolean) -> Unit,
    onDpadAction: (Int) -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val panelHeight = (220 * state.keyHeightScale).dp
    var selectMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(theme.keyboardBg)
    ) {
        PanelHeader(
            title = "Text Navigation"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT SIDE: D-Pad Console
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Background D-Pad ring shape or beautiful circular container
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .clip(CircleShape)
                        .background(theme.keyFaceDark.copy(alpha = 0.6f))
                        .then(
                            if (theme.hasBorder) Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.3f), CircleShape) else Modifier
                        )
                )

                // Layout our Up, Down, Left, Right around the Center SEL Toggle
                // Up Arrow
                DpadButton(
                    icon = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "Move Up",
                    onClick = { onMoveCursor(1, selectMode) },
                    theme = theme,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )

                // Down Arrow
                DpadButton(
                    icon = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Move Down",
                    onClick = { onMoveCursor(2, selectMode) },
                    theme = theme,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                )

                // Left Arrow
                DpadButton(
                    icon = Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = "Move Left",
                    onClick = { onMoveCursor(3, selectMode) },
                    theme = theme,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                )

                // Right Arrow
                DpadButton(
                    icon = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "Move Right",
                    onClick = { onMoveCursor(4, selectMode) },
                    theme = theme,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                )

                // Center SEL button with neon gradient glow when active
                val selGradient = Brush.linearGradient(
                    listOf(theme.accentGradientStart, theme.accentGradientEnd)
                )
                val selBg = if (selectMode) selGradient else Brush.linearGradient(listOf(theme.keyFaceDefault, theme.keyFaceDefault))
                val selBorder = if (selectMode) {
                    Modifier.border(2.dp, Brush.linearGradient(listOf(theme.accentGradientStart, theme.accentGradientEnd)), CircleShape)
                } else if (theme.hasBorder) {
                    Modifier.border(1.dp, theme.borderColor, CircleShape)
                } else {
                    Modifier
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(selBg)
                        .then(selBorder)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { selectMode = !selectMode }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SEL",
                        color = if (selectMode) CrispWhite else theme.keyText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // RIGHT SIDE: Editing Commands Grid
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CommandTile(
                        icon = Icons.Rounded.SelectAll,
                        label = "Select All",
                        onClick = { onDpadAction(1) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )
                    CommandTile(
                        icon = Icons.Rounded.ContentCopy,
                        label = "Copy",
                        onClick = { onDpadAction(2) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CommandTile(
                        icon = Icons.Rounded.ContentCut,
                        label = "Cut",
                        onClick = { onDpadAction(3) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )
                    CommandTile(
                        icon = Icons.Rounded.ContentPaste,
                        label = "Paste",
                        onClick = { onDpadAction(4) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CommandTile(
                        icon = Icons.Rounded.Undo,
                        label = "Undo",
                        onClick = { onDpadAction(5) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )
                    CommandTile(
                        icon = Icons.Rounded.Redo,
                        label = "Redo",
                        onClick = { onDpadAction(6) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DpadButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    theme: KeyboardTheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(theme.keyFaceDefault.copy(alpha = 0.85f))
            .then(
                if (theme.hasBorder) Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.5f), CircleShape) else Modifier
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = theme.keyText,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun CommandTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    theme: KeyboardTheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(theme.keyFaceDefault)
            .then(
                if (theme.hasBorder) Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp)) else Modifier
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = theme.accentGradientStart,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = theme.keyTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 3. AI Prompts Preset Panel — 3-category tabs + live search (Blueprint 27)
// ---------------------------------------------------------------------------
@Composable
fun AiPromptsPanel(
    state: KeyboardUiState,
    onPromptExecute: (String) -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val panelHeight = (240 * state.keyHeightScale).dp

    // Supabase DB live data
    val customPrompts by com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.prompts
        .collectAsState(initial = emptyList())

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { com.smafty.synapsekeyboard.data.local.SynapseDatabase.getInstance(context) }
    val mostUsed by db.mostUsedPromptDao().getTop20PromptsFlow()
        .collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.fetchPrompts()
    }

    var selectedTab  by remember { mutableStateOf(0) } // 0=Custom 1=Presets 2=Active
    var searchQuery  by remember { mutableStateOf("") }

    val tabLabels = listOf("⭐ Custom", "💎 Presets", "🔥 Active")

    // Hardcoded presets — same list as PromptsScreen for consistency
    val presets = listOf(
        PromptData("Fix Grammar",    "Fix all grammar and spelling mistakes while keeping the original meaning and tone.",             Icons.Rounded.Spellcheck),
        PromptData("Formal Tone",    "Rewrite this in a professional and formal tone suitable for a business email.",                  Icons.Rounded.BusinessCenter),
        PromptData("Friendly Tone",  "Rewrite this in a warm, friendly and conversational tone.",                                     Icons.Rounded.SentimentSatisfied),
        PromptData("Make Shorter",   "Shorten to key point in 1-2 concise sentences without losing the core message.",                Icons.Rounded.Compress),
        PromptData("Translate Urdu", "Translate this text accurately to Urdu script. Return only the translated text.",               Icons.Rounded.Translate),
        PromptData("Expand Text",    "Expand with more detail and depth while keeping the original intent.",                          Icons.Rounded.OpenInFull),
        PromptData("Bullet Points",  "Convert this text into a clean bullet-point list of key facts.",                               Icons.Rounded.List),
        PromptData("Email Subject",  "Generate a compelling email subject line for this text.",                                       Icons.Rounded.Email)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(theme.keyboardBg)
    ) {
        // Search bar row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(theme.toolbarBg)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = theme.keyTextMuted, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = theme.keyText, fontSize = 12.sp),
                decorationBox = { inner ->
                    if (searchQuery.isEmpty()) Text("Search prompts…", color = theme.keyTextMuted, fontSize = 12.sp)
                    inner()
                },
                modifier = Modifier.weight(1f)
            )
            if (searchQuery.isNotEmpty()) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Clear",
                    tint = theme.keyTextMuted,
                    modifier = Modifier.size(14.dp).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { searchQuery = "" }
                )
            }
        }

        // Tab row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(theme.toolbarBg)
                .padding(horizontal = 6.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabLabels.forEachIndexed { idx, label ->
                val selected = idx == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selected) theme.accentGradientStart.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { selectedTab = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) theme.accentGradientStart else theme.keyTextMuted, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // Content
        when (selectedTab) {
            0 -> { // Custom
                val filtered = customPrompts.filter {
                    searchQuery.isBlank() || it.title.contains(searchQuery, true) || it.prompt.contains(searchQuery, true)
                }
                if (filtered.isEmpty()) {
                    EmptyPanelState(if (searchQuery.isBlank()) "No custom prompts" else "No results", theme)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(filtered) { prompt ->
                            PromptRow(
                                title = prompt.title,
                                subtitle = prompt.prompt,
                                isSelected = state.selectedPromptInstruction == prompt.prompt,
                                theme = theme,
                                onClick = {
                                    if (state.selectedPromptInstruction == prompt.prompt) {
                                        onPromptExecute(prompt.prompt)
                                    } else {
                                        state.selectedPromptInstruction = prompt.prompt
                                    }
                                }
                            )
                        }
                    }
                }
            }
            1 -> { // Presets
                val filtered = presets.filter {
                    searchQuery.isBlank() || it.title.contains(searchQuery, true) || it.instruction.contains(searchQuery, true)
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(filtered) { prompt ->
                        PromptCard(
                            prompt = prompt,
                            isSelected = state.selectedPromptInstruction == prompt.instruction,
                            theme = theme,
                            onClick = {
                                if (state.selectedPromptInstruction == prompt.instruction) {
                                    onPromptExecute(prompt.instruction)
                                } else {
                                    state.selectedPromptInstruction = prompt.instruction
                                }
                            }
                        )
                    }
                }
            }
            2 -> { // Most Active
                val filtered = mostUsed.filter {
                    searchQuery.isBlank() || it.title.contains(searchQuery, true)
                }.take(10)
                if (filtered.isEmpty()) {
                    EmptyPanelState("No usage data yet", theme)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(filtered) { idx, prompt ->
                            PromptRow(
                                title = "${idx + 1}. ${prompt.title}",
                                subtitle = "Used ${prompt.useCount} times",
                                isSelected = state.selectedPromptInstruction == prompt.promptText,
                                theme = theme,
                                onClick = {
                                    if (state.selectedPromptInstruction == prompt.promptText) {
                                        onPromptExecute(prompt.promptText)
                                    } else {
                                        state.selectedPromptInstruction = prompt.promptText
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. Tool Manager Panel — customise the centre toolbar (Phase 2)
// ---------------------------------------------------------------------------
@Composable
private fun ToolManagerPanel(
    state: KeyboardUiState,
    onSaveAndExit: (List<String>) -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val panelHeight = (220 * state.keyHeightScale).dp

    // Gold colour for this panel's selected state
    val goldColor    = Color(0xFFD97706)
    val goldBrush    = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))

    // Local mutable selection mirroring state.visibleTools
    var selected by remember { mutableStateOf(state.visibleTools.toMutableList()) }

    // All available dynamic tools
    data class ToolEntry(val id: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
    val availableTools = listOf(
        ToolEntry(TOOL_CLIPBOARD,      "Clipboard",     Icons.Rounded.ContentPaste),
        ToolEntry(TOOL_TEXT_NAV,       "Text Nav",      Icons.Rounded.TextFormat),
        ToolEntry(TOOL_AI_PROMPTS,     "AI Prompts",    Icons.Rounded.AutoAwesome),
        ToolEntry(TOOL_THEME_SWITCHER, "Themes",        Icons.Rounded.Palette),
        ToolEntry(TOOL_SIZE_PANEL,     "Keyboard Size", Icons.Rounded.AspectRatio),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(theme.keyboardBg)
    ) {
        // Panel header with Done button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(theme.toolbarBg)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Configure Mid Toolbar",
                color = theme.keyText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            // Done button — accent gradient, pill shape
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(theme.accentGradientStart, theme.accentGradientEnd)
                        )
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onSaveAndExit(selected.toList()) }
                    )
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Done ✓",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Subtitle
        Text(
            text = "Select up to 5 tools (keyboard icon is always shown)",
            color = theme.keyTextMuted.copy(alpha = 0.7f),
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        // ── Permanent keyboard row (locked) ──────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.accentGradientStart.copy(alpha = 0.12f))
                    .border(1.5.dp, Brush.linearGradient(listOf(theme.accentGradientStart, theme.accentGradientEnd)), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.Keyboard,
                        contentDescription = "Keyboard",
                        tint = theme.accentGradientStart,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text("Keyboard", color = theme.accentGradientStart, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("Permanent · Always shown", color = theme.keyTextMuted, fontSize = 8.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = goldColor, modifier = Modifier.size(14.dp).padding(end = 4.dp))
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // ── Dynamic tools grid ────────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(availableTools) { tool ->
                val isSelected = tool.id in selected
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) goldColor.copy(alpha = 0.12f)
                            else theme.keyFaceDefault.copy(alpha = 0.7f)
                        )
                        .then(
                            if (isSelected)
                                Modifier.border(1.5.dp, goldBrush, RoundedCornerShape(10.dp))
                            else if (theme.hasBorder)
                                Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            else
                                Modifier
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                if (isSelected) {
                                    selected = (selected - tool.id).toMutableList()
                                } else {
                                    if (selected.size >= 5) {
                                        // Max reached — do nothing (UI shows locked state)
                                    } else {
                                        selected = (selected + tool.id).toMutableList()
                                    }
                                }
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    (if (isSelected) goldColor else theme.accentGradientStart).copy(alpha = 0.16f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.label,
                                tint = if (isSelected) goldColor else theme.accentGradientStart,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = tool.label,
                            color = if (isSelected) goldColor else theme.keyText,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Text(
                                text = "✓ Active",
                                color = goldColor.copy(alpha = 0.8f),
                                fontSize = 8.sp
                            )
                        } else if (!isSelected && selected.size >= 5) {
                            Text(
                                text = "Max reached",
                                color = theme.keyTextMuted.copy(alpha = 0.5f),
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPanelState(message: String, theme: KeyboardTheme) {
    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SearchOff,
                contentDescription = null,
                tint = theme.keyTextMuted.copy(alpha = 0.4f),
                modifier = Modifier.size(32.dp)
            )
            Text(message, color = theme.keyTextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------------------
// 5. Mini Theme Switcher Panel — Inline circular swatch selector
// ---------------------------------------------------------------------------
@Composable
private fun MiniThemeSwitcherPanel(
    state: KeyboardUiState,
    onThemeSelected: (KeyboardTheme) -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val panelHeight = (130 * state.keyHeightScale).dp
    val allThemes = KeyboardTheme.entries

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(theme.keyboardBg)
    ) {
        PanelHeader(title = "Theme")

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(allThemes) { t ->
                val isSelected = t == state.activeTheme
                val borderBrush = if (isSelected) {
                    Brush.linearGradient(listOf(t.accentGradientStart, t.accentGradientEnd))
                } else {
                    Brush.linearGradient(listOf(theme.borderColor, theme.borderColor))
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onThemeSelected(t) }
                        )
                ) {
                    // Outer ring for selected state
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 52.dp else 46.dp)
                            .clip(CircleShape)
                            .border(if (isSelected) 2.5.dp else 1.dp, borderBrush, CircleShape)
                            .padding(3.dp)
                    ) {
                        // Half-half color preview circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(t.accentGradientStart, t.accentGradientEnd)
                                    )
                                )
                        ) {
                            // Key face preview stripe
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.4f)
                                    .align(Alignment.BottomCenter)
                                    .background(t.keyboardBg.copy(alpha = 0.7f))
                            )
                        }
                        // Active checkmark
                        if (isSelected) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = t.displayName.split(" ").first(),
                        color = if (isSelected) theme.accentGradientStart else theme.keyTextMuted,
                        fontSize = 8.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 6. Size Selector Panel — Inline slider to resize keyboard height
// ---------------------------------------------------------------------------
@Composable
private fun SizeSelectorPanel(
    state: KeyboardUiState,
    onSizeSelected: (Float) -> Unit
) {
    val theme = LocalKeyboardTheme.current
    val panelHeight = (130 * state.keyHeightScale).dp

    // Size presets: (label, scale value)
    val presets = listOf(
        "XS" to 0.80f,
        "S"  to 0.90f,
        "M"  to 1.00f,
        "L"  to 1.15f,
        "XL" to 1.30f,
    )
    val currentScale = state.keyHeightScale

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(panelHeight)
            .background(theme.keyboardBg)
    ) {
        PanelHeader(title = "Keyboard Size")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            presets.forEach { (label, scale) ->
                val isActive = currentScale == scale
                val gradientBrush = Brush.linearGradient(
                    listOf(theme.accentGradientStart, theme.accentGradientEnd)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isActive) theme.accentGradientStart.copy(alpha = 0.18f)
                            else theme.keyFaceDefault
                        )
                        .then(
                            if (isActive)
                                Modifier.border(2.dp, gradientBrush, RoundedCornerShape(10.dp))
                            else if (theme.hasBorder)
                                Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            else Modifier
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onSizeSelected(scale) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            color = if (isActive) theme.accentGradientStart else theme.keyText,
                            fontSize = 14.sp,
                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
                        )
                        Text(
                            text = "${(scale * 100).toInt()}%",
                            color = if (isActive) theme.accentGradientEnd else theme.keyTextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromptRow(
    title: String,
    subtitle: String,
    isSelected: Boolean = false,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    val borderMod = if (isSelected) {
        Modifier.border(1.5.dp, ElectricPurple, RoundedCornerShape(6.dp))
    } else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) ElectricPurple.copy(alpha = 0.14f)
                else theme.keyFaceDefault.copy(alpha = 0.7f)
            )
            .then(borderMod)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = if (isSelected) ElectricPurple else theme.keyText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = if (isSelected) "Tap again to run ✨" else subtitle,
                color = if (isSelected) ElectricPurple.copy(alpha = 0.8f) else theme.keyTextMuted,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            if (isSelected) Icons.Rounded.PlayArrow else Icons.Rounded.ArrowForwardIos,
            contentDescription = null,
            tint = if (isSelected) ElectricPurple else theme.keyTextMuted.copy(alpha = 0.4f),
            modifier = Modifier.size(if (isSelected) 16.dp else 10.dp)
        )
    }
}

private data class PromptData(
    val title: String,
    val instruction: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun PromptCard(
    prompt: PromptData,
    isSelected: Boolean = false,
    theme: KeyboardTheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) ElectricPurple.copy(alpha = 0.14f)
                else theme.keyFaceDefault.copy(alpha = 0.7f)
            )
            .then(
                if (isSelected)
                    Modifier.border(1.5.dp, ElectricPurple, RoundedCornerShape(8.dp))
                else if (theme.hasBorder)
                    Modifier.border(1.dp, theme.borderColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(
                    (if (isSelected) ElectricPurple else theme.accentGradientStart).copy(alpha = 0.15f)
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Rounded.PlayArrow else prompt.icon,
                    contentDescription = prompt.title,
                    tint = if (isSelected) ElectricPurple else theme.accentGradientStart,
                    modifier = Modifier.size(14.dp)
                )
            }
            Column {
                Text(
                    prompt.title,
                    color = if (isSelected) ElectricPurple else theme.keyText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                val instructionText = if (isSelected) "Tap again to run" else prompt.instruction
                Text(
                    text = instructionText,
                    color = if (isSelected) ElectricPurple.copy(alpha = 0.8f) else theme.keyTextMuted,
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
