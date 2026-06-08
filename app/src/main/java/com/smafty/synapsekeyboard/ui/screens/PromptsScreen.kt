package com.smafty.synapsekeyboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.data.local.SynapseDatabase
import com.smafty.synapsekeyboard.data.local.entity.CustomPromptEntity
import com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.EmeraldGreen
import com.smafty.synapsekeyboard.ui.theme.GlassmorphismColor
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import com.smafty.synapsekeyboard.ui.theme.TextColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ── Built-in preset prompts (hardcoded) ──────────────────────────────────────
data class PresetPrompt(val id: String, val title: String, val instruction: String)

val SYNAPSE_PRESETS = listOf(
    PresetPrompt("fix_grammar",   "Fix Grammar",       "Fix all grammar and spelling mistakes while keeping the original meaning and tone."),
    PresetPrompt("translate_en",  "Translate to Urdu", "Translate this text accurately to Urdu script. Return only the translated text."),
    PresetPrompt("formal_tone",   "Formal Tone",       "Rewrite this in a professional and formal tone suitable for a business email."),
    PresetPrompt("make_shorter",  "Make Shorter",      "Shorten this text to its key point in 1-2 concise sentences without losing the core message."),
    PresetPrompt("friendly_tone", "Friendly Tone",     "Rewrite this in a warm, friendly and conversational tone."),
    PresetPrompt("expand_text",   "Expand Text",       "Expand this text with more detail and depth while keeping the original intent."),
    PresetPrompt("bullet_points", "Bullet Points",     "Convert this text into a clean bullet-point list of key facts."),
    PresetPrompt("subject_line",  "Email Subject",     "Generate a compelling email subject line for this text.")
)

// ── Tabs ────────────────────────────────--------------------------------────
private enum class PromptsTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    CUSTOM("Custom",      Icons.Rounded.Star),
    PRESETS("Presets",    Icons.Rounded.Verified),
    MOST_ACTIVE("Active", Icons.Rounded.Whatshot)
}

// ── Screen ────────────────------------------------------------------------──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptsScreen() {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val db      = remember { SynapseDatabase.getInstance(context) }

    // Live data from Supabase repository
    val customPrompts by com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.prompts.collectAsState(initial = emptyList())
    val mostUsed      by db.mostUsedPromptDao().getTop20PromptsFlow().collectAsState(initial = emptyList())

    var selectedTab   by remember { mutableStateOf(PromptsTab.CUSTOM) }
    var searchQuery   by remember { mutableStateOf("") }
    var showSheet     by remember { mutableStateOf(false) }
    var editingPrompt by remember { mutableStateOf<com.smafty.synapsekeyboard.data.local.repository.RemoteCustomPrompt?>(null) }

    var showDeleteAllDialog  by remember { mutableStateOf(false) }
    var deleteAllConfirmText by remember { mutableStateOf("") }
    var showResetUsageDialog by remember { mutableStateOf(false) }
    var promptToDelete       by remember { mutableStateOf<com.smafty.synapsekeyboard.data.local.repository.RemoteCustomPrompt?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var visible    by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        isLoading = true
        com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.fetchPrompts()
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(28.dp))

            // ── Header with accent strip ──────────────────────────────────────
            AnimatedVisibility(visible, enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -24 }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
                        Text("My Prompts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = TextColor)
                        Spacer(Modifier.height(3.dp))
                        Text("${customPrompts.size} custom · ${SYNAPSE_PRESETS.size} presets", style = MaterialTheme.typography.bodyMedium, color = MutedGrey.copy(alpha = 0.75f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Refresh button
                        if (isLoading) {
                            val infiniteTransition = rememberInfiniteTransition(label = "spin")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f, targetValue = 360f,
                                animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Restart),
                                label = "rotation"
                            )
                            Icon(
                                Icons.Rounded.Refresh, contentDescription = "Loading",
                                tint = ElectricPurple, modifier = Modifier.size(22.dp).rotate(rotation)
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        isLoading = true
                                        com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.fetchPrompts()
                                        isLoading = false
                                    }
                                }
                            ) {
                                Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = MutedGrey.copy(alpha = 0.65f))
                            }
                        }
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "More", tint = MutedGrey.copy(alpha = 0.65f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Search bar ────────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search prompts…", color = MutedGrey.copy(alpha = 0.45f), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MutedGrey.copy(alpha = 0.55f), modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = MutedGrey.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricPurple,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                    focusedContainerColor = GlassmorphismColor.copy(alpha = 0.12f),
                    unfocusedContainerColor = GlassmorphismColor,
                    focusedTextColor = TextColor,
                    unfocusedTextColor = TextColor,
                    cursorColor = ElectricPurple
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            )

            Spacer(Modifier.height(16.dp))

            // ── Pill Segment Switcher ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PromptsTab.values().forEach { tab ->
                    val selected = tab == selectedTab
                    val activeBg = Brush.linearGradient(listOf(ElectricPurple, ElectricPurple.copy(red = ElectricPurple.red * 0.85f)))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .shadow(if (selected) 4.dp else 0.dp, RoundedCornerShape(12.dp), spotColor = ElectricPurple.copy(0.3f))
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) ElectricPurple.copy(alpha = 0.12f) else Color.Transparent)
                            .border(
                                width = if (selected) 1.dp else 0.dp,
                                color = if (selected) ElectricPurple.copy(alpha = 0.35f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector        = tab.icon,
                                contentDescription = null,
                                tint               = if (selected) ElectricPurple else MutedGrey.copy(0.6f),
                                modifier           = Modifier.size(14.dp)
                            )
                            Text(
                                text       = tab.label,
                                color      = if (selected) TextColor else MutedGrey.copy(0.6f),
                                fontSize   = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Content ───────────────────────────────────────────────────────
            when (selectedTab) {
                PromptsTab.CUSTOM -> {
                    val filtered = customPrompts.filter { p ->
                        searchQuery.isBlank() || p.title.contains(searchQuery, ignoreCase = true) || p.prompt.contains(searchQuery, ignoreCase = true)
                    }
                    if (filtered.isEmpty()) {
                        EmptyState(
                            title = if (searchQuery.isBlank()) "No custom prompts yet" else "No results for \"$searchQuery\"",
                            subtitle = if (searchQuery.isBlank()) "Tap + to add your first AI prompt" else "Try a different keyword",
                            showAdd = searchQuery.isBlank(),
                            onAdd = { editingPrompt = null; showSheet = true }
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp)) {
                            itemsIndexed(filtered, key = { _, it -> it.id }) { idx, prompt ->
                                CustomPromptCard(
                                    prompt   = prompt,
                                    index    = idx,
                                    onEdit   = { editingPrompt = prompt; showSheet = true },
                                    onDelete = { promptToDelete = prompt }
                                )
                            }
                        }
                    }
                }
                PromptsTab.PRESETS -> {
                    val filtered = SYNAPSE_PRESETS.filter { p ->
                        searchQuery.isBlank() || p.title.contains(searchQuery, ignoreCase = true) || p.instruction.contains(searchQuery, ignoreCase = true)
                    }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 28.dp)) {
                        itemsIndexed(filtered, key = { _, it -> it.id }) { idx, preset ->
                            PresetPromptCard(preset, idx)
                        }
                    }
                }
                PromptsTab.MOST_ACTIVE -> {
                    val filtered = mostUsed.filter { p ->
                        searchQuery.isBlank() || p.title.contains(searchQuery, ignoreCase = true)
                    }.take(10)
                    if (filtered.isEmpty()) {
                        EmptyState("No usage data yet", "Use prompts from the keyboard to track your favourites", showAdd = false, onAdd = {})
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp)) {
                            itemsIndexed(filtered, key = { _, it -> it.promptId }) { idx, prompt ->
                                MostActiveCard(prompt, idx, onResetUsage = { showResetUsageDialog = true })
                            }
                        }
                    }
                }
            }
        }

        // FAB — only on Custom tab
        if (selectedTab == PromptsTab.CUSTOM) {
            FloatingActionButton(
                onClick = { editingPrompt = null; showSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
                    .shadow(12.dp, CircleShape, spotColor = ElectricPurple.copy(alpha = 0.5f)),
                shape = CircleShape,
                containerColor = ElectricPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Prompt", tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
    }

    // ── Add/Edit bottom sheet ─────────────────────────────────────────────────
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = DeepSlate,
            windowInsets = BottomSheetDefaults.windowInsets.union(WindowInsets.ime),
            dragHandle = {
                Box(
                    Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(MutedGrey.copy(alpha = 0.35f))
                )
            },
            modifier = Modifier.border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
        ) {
            PromptBottomSheet(
                editingPrompt = editingPrompt,
                onSave = { title, instruction ->
                    scope.launch {
                        if (editingPrompt != null) {
                            com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.updatePrompt(editingPrompt!!.id, title, instruction)
                        } else {
                            com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.addPrompt(title, instruction)
                        }
                    }
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showSheet = false }
                }
            )
        }
    }

    // ── Single prompt delete confirmation ─────────────────────────────────────
    promptToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { promptToDelete = null },
            title = { Text("Delete Prompt?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("\"${target.title}\" will be permanently deleted.", color = MutedGrey.copy(0.8f), fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.deletePrompt(target.id) }
                    promptToDelete = null
                }) { Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { promptToDelete = null }) { Text("Cancel", color = MutedGrey) }
            },
            containerColor = DeepSlate,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(22.dp))
        )
    }

    // ── Delete ALL custom prompts ─────────────────────────────────────────────
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false; deleteAllConfirmText = "" },
            title = { Text("Delete All Custom Prompts?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    Text("This cannot be undone. Type Delete below to confirm.", color = MutedGrey.copy(0.8f), fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = deleteAllConfirmText,
                        onValueChange = { deleteAllConfirmText = it },
                        placeholder = { Text("Type: Delete", color = MutedGrey.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = MutedGrey.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch { com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.deleteAllPrompts() }
                        showDeleteAllDialog = false; deleteAllConfirmText = ""
                    },
                    enabled = deleteAllConfirmText == "Delete"
                ) {
                    Text(
                        text       = "Confirm Delete",
                        color      = if (deleteAllConfirmText == "Delete") Color(0xFFEF4444) else MutedGrey.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false; deleteAllConfirmText = "" }) { Text("Cancel", color = MutedGrey) }
            },
            containerColor = DeepSlate,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(22.dp))
        )
    }

    // ── Reset Most Active usage ───────────────────────────────────────────────
    if (showResetUsageDialog) {
        AlertDialog(
            onDismissRequest = { showResetUsageDialog = false },
            title = { Text("Reset Prompt History?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text("This will clear all usage frequency data. Your custom prompts will not be affected.", color = MutedGrey.copy(0.8f), fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch(Dispatchers.IO) { db.mostUsedPromptDao().deleteAll() }
                    showResetUsageDialog = false
                }) { Text("Reset", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetUsageDialog = false }) { Text("Cancel", color = MutedGrey) }
            },
            containerColor = DeepSlate,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(22.dp))
        )
    }
}

// ── Custom prompt card ────────────────────────────────────────────────────────
@Composable
private fun CustomPromptCard(
    prompt: com.smafty.synapsekeyboard.data.local.repository.RemoteCustomPrompt,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColors = listOf(ElectricPurple, EmeraldGreen, Color(0xFF06B6D4), Color(0xFFF59E0B), Color(0xFFEC4899))
    val accent = accentColors[index % accentColors.size]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = ElectricPurple.copy(alpha = 0.16f),
                ambientColor = ElectricPurple.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(GlassmorphismColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(62.dp)
                .background(
                    Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.3f)))
                )
        )
        Spacer(Modifier.width(14.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text("${index + 1}", color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 14.dp)
        ) {
            Text(prompt.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(prompt.prompt, style = MaterialTheme.typography.bodySmall, color = MutedGrey.copy(alpha = 0.70f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = MutedGrey.copy(alpha = 0.50f), modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.70f), modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(4.dp))
    }
}

// ── Preset card ───────────────────────────────────────────────────────────────
@Composable
private fun PresetPromptCard(preset: PresetPrompt, index: Int) {
    val accentColors = listOf(ElectricPurple, EmeraldGreen, Color(0xFF06B6D4), Color(0xFFF59E0B), Color(0xFFEC4899))
    val accent = accentColors[index % accentColors.size]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = ElectricPurple.copy(alpha = 0.16f),
                ambientColor = ElectricPurple.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(GlassmorphismColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(62.dp)
                .background(
                    Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.3f)))
                )
        )
        Spacer(Modifier.width(14.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Rounded.Verified,
                contentDescription = null,
                tint               = accent,
                modifier           = Modifier.size(17.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 14.dp)
        ) {
            Text(preset.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(preset.instruction, style = MaterialTheme.typography.bodySmall, color = MutedGrey.copy(alpha = 0.70f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(accent.copy(alpha = 0.13f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Built-in", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
    }
}

// ── Most Active card ──────────────────────────────────────────────────────────
@Composable
private fun MostActiveCard(prompt: MostUsedPromptEntity, index: Int, onResetUsage: () -> Unit) {
    val amber = Color(0xFFF59E0B)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = ElectricPurple.copy(alpha = 0.16f),
                ambientColor = ElectricPurple.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(GlassmorphismColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(62.dp)
                .background(
                    Brush.verticalGradient(listOf(amber, amber.copy(alpha = 0.3f)))
                )
        )
        Spacer(Modifier.width(14.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(amber.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text("#${index + 1}", color = amber, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 14.dp)
        ) {
            Text(prompt.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Used ${prompt.useCount} times", style = MaterialTheme.typography.bodySmall, color = MutedGrey.copy(alpha = 0.70f))
        }
        if (index == 0) {
            IconButton(onClick = onResetUsage, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Reset usage", tint = MutedGrey.copy(alpha = 0.40f), modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
    }
}

// ── Empty state ────────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(title: String, subtitle: String, showAdd: Boolean, onAdd: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(top = 70.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(12.dp, RoundedCornerShape(26.dp), spotColor = ElectricPurple.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.radialGradient(
                        listOf(ElectricPurple.copy(alpha = 0.18f), ElectricPurple.copy(alpha = 0.04f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint               = ElectricPurple,
                modifier           = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = TextColor)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MutedGrey.copy(alpha = 0.75f), modifier = Modifier.padding(top = 4.dp))
        if (showAdd) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onAdd,
                colors  = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                shape   = RoundedCornerShape(14.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add Prompt", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Add/Edit bottom sheet ─────────────────────────────────────────────────────
@Composable
private fun PromptBottomSheet(
    editingPrompt: com.smafty.synapsekeyboard.data.local.repository.RemoteCustomPrompt?,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var titleText       by remember { mutableStateOf(editingPrompt?.title ?: "") }
    var instructionText by remember { mutableStateOf(editingPrompt?.prompt ?: "") }
    val titleError = titleText.isNotEmpty() && titleText.length > 16
    val isValid = titleText.isNotBlank() && instructionText.isNotBlank() && !titleError

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (editingPrompt != null) "Edit Prompt" else "New Prompt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
            IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, contentDescription = "Close", tint = MutedGrey) }
        }
        Spacer(Modifier.height(20.dp))
        Text("Short Title", style = MaterialTheme.typography.labelLarge, color = MutedGrey, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = titleText,
            onValueChange = { if (it.length <= 20) titleText = it },
            placeholder = { Text("e.g. Fix Grammar", color = MutedGrey.copy(alpha = 0.5f)) },
            isError = titleError,
            supportingText = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (titleError) Text("Max 16 chars for keyboard display", color = Color(0xFFEF4444), fontSize = 11.sp) else Spacer(Modifier.weight(1f))
                    Text("${titleText.length}/16", color = MutedGrey.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricPurple,
                unfocusedBorderColor = MutedGrey.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = ElectricPurple,
                errorBorderColor = Color(0xFFEF4444)
            ),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("Full Instruction", style = MaterialTheme.typography.labelLarge, color = MutedGrey, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = instructionText,
            onValueChange = { instructionText = it },
            placeholder = { Text("Describe what the AI should do with the text…", color = MutedGrey.copy(alpha = 0.5f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricPurple,
                unfocusedBorderColor = MutedGrey.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = ElectricPurple
            ),
            shape = RoundedCornerShape(14.dp),
            minLines = 3, maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { if (isValid) onSave(titleText.trim(), instructionText.trim()) },
            enabled = isValid,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, disabledContainerColor = ElectricPurple.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (editingPrompt != null) "Save Changes" else "Add Prompt", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}
