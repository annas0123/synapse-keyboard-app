package com.smafty.synapsekeyboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.LinearOutSlowInEasing

// ── Premium Minimal design tokens ────────────────────────────────────────────
private val PR_Bg      = Color(0xFF0A0A0F)
private val PR_Surface = Color(0xFF141420)
private val PR_Border  = Color(0xFF2A2A3A)
private val PR_Violet  = Color(0xFF7C5CFC)
private val PR_Text    = Color(0xFFF0F0F5)
private val PR_Muted   = Color(0xFF8888A0)
private val PR_Amber   = Color(0xFFFBBF24) // reference sparkle yellow

// ── Built-in preset prompts (hardcoded) ──────────────────────────────────────
data class PresetPrompt(val id: String, val title: String, val instruction: String)

val SYNAPSE_PRESETS = listOf(
    PresetPrompt("fix_grammar",   "Fix Grammar",       "Fix all grammar and spelling mistakes while keeping the original meaning and tone."),
    PresetPrompt("translate_en",  "Translate to Urdu", "Translate this text accurately into natural, correct Urdu (اردو). Keep the meaning exact and the tone the same. Return only the translated text."),
    PresetPrompt("formal_tone",   "Formal Tone",       "Rewrite this in a professional and formal tone suitable for a business email."),
    PresetPrompt("make_shorter",  "Make Shorter",      "Shorten this text to its key point in 1-2 concise sentences without losing the core message."),
    PresetPrompt("friendly_tone", "Friendly Tone",     "Rewrite this in a warm, friendly and conversational tone."),
    PresetPrompt("expand_text",   "Expand Text",       "Expand this text with more detail and depth while keeping the original intent."),
    PresetPrompt("bullet_points", "Bullet Points",     "Convert this text into a clean bullet-point list of key facts."),
    PresetPrompt("subject_line",  "Email Subject",     "Generate a compelling email subject line for this text."),
    // ── Real-time use case presets (see jawab_use_cases.md Drafts 1–4, 6) ──
    PresetPrompt("reply_client",  "Reply to Client",           "Read the client's message above and write a professional, polite reply that addresses their question directly, sounds confident and friendly, and is short. Match the language the client wrote in."),
    PresetPrompt("match_job",     "Match Job & Write Proposal","I will give my details and a job's requirements below. Write a short, professional proposal or cover message that highlights how my skills match the job, sounds confident, and is ready to send on LinkedIn, Indeed, or any hiring platform."),
    PresetPrompt("reply_letter",  "Reply to Letter or Email",  "Read the letter or email above and write a clear, professional reply that answers everything it asks. Keep the tone polite and formal, and keep it short."),
    PresetPrompt("reddit_comment","Reddit Comment",            "Write a thoughtful, natural-sounding Reddit comment about the topic above. Make it friendly, conversational, and add value to the discussion. Avoid sounding like an ad or a bot."),
    PresetPrompt("translate_any", "Translate (Any Language)",  "Translate the text above into the language I name. Keep the meaning exact and the tone natural for a native speaker. If I don't name a language, ask me which language in one short line.")
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PR_Bg)
    ) {
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
                        Text(
                            text       = "My Prompts",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = PR_Text
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text  = "${customPrompts.size} custom · ${SYNAPSE_PRESETS.size} presets",
                            fontSize = 13.sp,
                            color = PR_Muted
                        )
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
                                tint = PR_Violet, modifier = Modifier.size(22.dp).rotate(rotation)
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
                                Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = PR_Muted)
                            }
                        }
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "More", tint = PR_Muted)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Search bar ────────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search prompts…", color = PR_Muted, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = PR_Muted, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = PR_Muted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PR_Violet,
                    unfocusedBorderColor = PR_Border,
                    focusedContainerColor = PR_Surface,
                    unfocusedContainerColor = PR_Surface,
                    focusedTextColor = PR_Text,
                    unfocusedTextColor = PR_Text,
                    cursorColor = PR_Violet
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            )

            Spacer(Modifier.height(16.dp))

            // ── Pill Segment Switcher ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PR_Surface)
                    .border(0.5.dp, PR_Border, RoundedCornerShape(20.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PromptsTab.values().forEach { tab ->
                    val selected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) PR_Violet else Color.Transparent)
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
                                tint               = if (selected) Color.White else PR_Muted,
                                modifier           = Modifier.size(14.dp)
                            )
                            Text(
                                text       = tab.label,
                                color      = if (selected) Color.White else PR_Muted,
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

        // FAB — only on Custom tab (200ms fade + scale in/out)
        AnimatedVisibility(
            visible = selectedTab == PromptsTab.CUSTOM,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0f, animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0f, animationSpec = tween(200)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            FloatingActionButton(
                onClick = { editingPrompt = null; showSheet = true },
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape, spotColor = PR_Violet.copy(alpha = 0.4f)),
                shape = CircleShape,
                containerColor = PR_Violet,
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
            containerColor = PR_Surface,
            windowInsets = BottomSheetDefaults.windowInsets.union(WindowInsets.ime),
            dragHandle = {
                Box(
                    Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(PR_Muted.copy(alpha = 0.35f))
                )
            },
            modifier = Modifier.border(0.5.dp, PR_Border, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
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
            title = { Text("Delete Prompt?", fontWeight = FontWeight.Bold, color = PR_Text) },
            text = { Text("\"${target.title}\" will be permanently deleted.", color = PR_Muted, fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { com.smafty.synapsekeyboard.data.local.repository.CustomPromptRepository.deletePrompt(target.id) }
                    promptToDelete = null
                }) { Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { promptToDelete = null }) { Text("Cancel", color = PR_Muted) }
            },
            containerColor = PR_Surface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(0.5.dp, PR_Border, RoundedCornerShape(16.dp))
        )
    }

    // ── Delete ALL custom prompts ─────────────────────────────────────────────
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false; deleteAllConfirmText = "" },
            title = { Text("Delete All Custom Prompts?", fontWeight = FontWeight.Bold, color = PR_Text) },
            text = {
                Column {
                    Text("This cannot be undone. Type Delete below to confirm.", color = PR_Muted, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = deleteAllConfirmText,
                        onValueChange = { deleteAllConfirmText = it },
                        placeholder = { Text("Type: Delete", color = PR_Muted.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = PR_Border,
                            focusedTextColor = PR_Text,
                            unfocusedTextColor = PR_Text,
                            cursorColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(10.dp),
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
                        color      = if (deleteAllConfirmText == "Delete") Color(0xFFEF4444) else PR_Muted.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false; deleteAllConfirmText = "" }) { Text("Cancel", color = PR_Muted) }
            },
            containerColor = PR_Surface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(0.5.dp, PR_Border, RoundedCornerShape(16.dp))
        )
    }

    // ── Reset Most Active usage ───────────────────────────────────────────────
    if (showResetUsageDialog) {
        AlertDialog(
            onDismissRequest = { showResetUsageDialog = false },
            title = { Text("Reset Prompt History?", fontWeight = FontWeight.Bold, color = PR_Text) },
            text = { Text("This will clear all usage frequency data. Your custom prompts will not be affected.", color = PR_Muted, fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch(Dispatchers.IO) { db.mostUsedPromptDao().deleteAll() }
                    showResetUsageDialog = false
                }) { Text("Reset", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetUsageDialog = false }) { Text("Cancel", color = PR_Muted) }
            },
            containerColor = PR_Surface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.border(0.5.dp, PR_Border, RoundedCornerShape(16.dp))
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
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "cAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 16f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "cSlide"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = alpha, translationY = translationY)
            .clip(RoundedCornerShape(12.dp))
            .background(PR_Surface)
            .border(0.5.dp, PR_Border, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip (4dp)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .background(PR_Violet)
        )
        Spacer(Modifier.width(14.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PR_Violet.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("${index + 1}", color = PR_Violet, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Text(prompt.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PR_Text)
            Spacer(modifier = Modifier.height(2.dp))
            Text(prompt.prompt, fontSize = 12.sp, color = PR_Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = PR_Muted, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(4.dp))
    }
}

// ── Preset card ───────────────────────────────────────────────────────────────
@Composable
private fun PresetPromptCard(preset: PresetPrompt, index: Int) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "pAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 16f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "pSlide"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = alpha, translationY = translationY)
            .clip(RoundedCornerShape(12.dp))
            .background(PR_Surface)
            .border(0.5.dp, PR_Border, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip (4dp)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .background(PR_Violet)
        )
        Spacer(Modifier.width(14.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PR_Violet.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Rounded.Verified,
                contentDescription = null,
                tint               = PR_Violet,
                modifier           = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Text(preset.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PR_Text)
            Spacer(modifier = Modifier.height(2.dp))
            Text(preset.instruction, fontSize = 12.sp, color = PR_Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PR_Violet.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("Built-in", color = PR_Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
    }
}

// ── Most Active card ──────────────────────────────────────────────────────────
@Composable
private fun MostActiveCard(prompt: MostUsedPromptEntity, index: Int, onResetUsage: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "aAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 16f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "aSlide"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = alpha, translationY = translationY)
            .clip(RoundedCornerShape(12.dp))
            .background(PR_Surface)
            .border(0.5.dp, PR_Border, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent strip (4dp)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .background(PR_Amber)
        )
        Spacer(Modifier.width(14.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PR_Amber.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("#${index + 1}", color = PR_Amber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Text(prompt.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PR_Text)
            Spacer(modifier = Modifier.height(2.dp))
            Text("Used ${prompt.useCount} times", fontSize = 12.sp, color = PR_Muted)
        }
        if (index == 0) {
            IconButton(onClick = onResetUsage, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Reset usage", tint = PR_Muted, modifier = Modifier.size(18.dp))
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
                .clip(RoundedCornerShape(20.dp))
                .background(PR_Violet.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint               = PR_Violet,
                modifier           = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PR_Text)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, fontSize = 14.sp, color = PR_Muted, modifier = Modifier.padding(top = 4.dp))
        if (showAdd) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onAdd,
                colors  = ButtonDefaults.buttonColors(containerColor = PR_Violet),
                shape   = RoundedCornerShape(12.dp),
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
    // Single source of truth: title may be up to 30 chars everywhere (input, error, counter).
    val titleMaxChars = 30
    val titleError = titleText.isNotEmpty() && titleText.length > titleMaxChars
    val isValid = titleText.isNotBlank() && instructionText.isNotBlank() && !titleError
    val context = LocalContext.current
    val clipboardManager = androidx.core.content.ContextCompat.getSystemService(
        context, android.content.ClipboardManager::class.java
    )

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
            Text(if (editingPrompt != null) "Edit Prompt" else "New Prompt", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PR_Text)
            IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, contentDescription = "Close", tint = PR_Muted) }
        }
        Spacer(Modifier.height(20.dp))
        Text("Short Title", fontSize = 14.sp, color = PR_Muted, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = titleText,
            onValueChange = { if (it.length <= titleMaxChars) titleText = it },
            placeholder = { Text("e.g. Fix Grammar", color = PR_Muted.copy(alpha = 0.5f)) },
            isError = titleError,
            trailingIcon = {
                PasteIconButton {
                    val clip = clipboardManager?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
                    if (clip.isNotBlank()) titleText = clip.take(titleMaxChars)
                }
            },
            supportingText = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (titleError) Text("Max $titleMaxChars chars for keyboard display", color = Color(0xFFEF4444), fontSize = 11.sp) else Spacer(Modifier.weight(1f))
                    Text("${titleText.length}/$titleMaxChars", color = PR_Muted.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PR_Violet,
                unfocusedBorderColor = PR_Border,
                focusedTextColor = PR_Text,
                unfocusedTextColor = PR_Text,
                cursorColor = PR_Violet,
                errorBorderColor = Color(0xFFEF4444)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("Full Instruction", fontSize = 14.sp, color = PR_Muted, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = instructionText,
            onValueChange = { instructionText = it },
            placeholder = { Text("Describe what the AI should do with the text…", color = PR_Muted.copy(alpha = 0.5f)) },
            trailingIcon = {
                PasteIconButton(label = "Paste", withLabel = true) {
                    val clip = clipboardManager?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
                    if (clip.isNotBlank()) {
                        instructionText = if (instructionText.isBlank()) clip.trim() else "${instructionText.trimEnd()}\n$clip"
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PR_Violet,
                unfocusedBorderColor = PR_Border,
                focusedTextColor = PR_Text,
                unfocusedTextColor = PR_Text,
                cursorColor = PR_Violet
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = 3, maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = { if (isValid) onSave(titleText.trim(), instructionText.trim()) },
            enabled = isValid,
            colors = ButtonDefaults.buttonColors(containerColor = PR_Violet, disabledContainerColor = PR_Violet.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(if (editingPrompt != null) "Save Changes" else "Add Prompt", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Paste icon button ──────────────────────────────────────────────────────────
// Android IME long-press paste is unreliable when Synapse is the active keyboard.
// This explicit button reads the system clipboard on tap instead. Used on both the
// Title and Full Instruction fields in the add/edit bottom sheet.
@Composable
private fun PasteIconButton(
    label: String = "Paste",
    withLabel: Boolean = false,
    onPaste: () -> Unit
) {
    if (withLabel) {
        Row(
            modifier = Modifier
                .padding(end = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PR_Violet.copy(alpha = 0.12f))
                .border(1.dp, PR_Violet.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .clickable(onClick = onPaste)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Rounded.ContentPaste, contentDescription = label,
                tint = PR_Violet, modifier = Modifier.size(15.dp)
            )
            Text(label, color = PR_Violet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        IconButton(onClick = onPaste) {
            Icon(
                Icons.Rounded.ContentPaste, contentDescription = label,
                tint = PR_Muted, modifier = Modifier.size(18.dp)
            )
        }
    }
}
