package com.smafty.synapsekeyboard.ui.screens

import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.smafty.synapsekeyboard.auth.AuthManager
import com.smafty.synapsekeyboard.auth.SupabaseClientProvider
import com.smafty.synapsekeyboard.data.local.SynapseDatabase
import com.smafty.synapsekeyboard.data.local.repository.EnergyQuotaRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ── Premium Minimal design tokens ────────────────────────────────────────────
private val HS_Bg      = Color(0xFF0A0A0F)
private val HS_Surface = Color(0xFF141420)
private val HS_Border  = Color(0xFF2A2A3A)
private val HS_Violet  = Color(0xFF7C5CFC)
private val HS_Text    = Color(0xFFF0F0F5)
private val HS_Muted   = Color(0xFF8888A0)
private val HS_Success = Color(0xFF34D399)

// ---------------------------------------------------------------------------
// Home Screen — all backend logic unchanged, UI layer upgraded
// ---------------------------------------------------------------------------
@Composable
fun HomeScreen() {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isKeyboardEnabled = remember { mutableStateOf(false) }
    val isKeyboardDefault = remember { mutableStateOf(false) }

    fun refreshKeyboardStatus() {
        val imm         = context.getSystemService(InputMethodManager::class.java)
        val packageName = context.packageName
        isKeyboardEnabled.value = imm?.enabledInputMethodList
            ?.any { it.packageName == packageName } == true
        isKeyboardDefault.value = try {
            val defaultIme = Settings.Secure.getString(
                context.contentResolver, "default_input_method"
            )
            defaultIme?.contains(packageName) == true
        } catch (e: Exception) {
            false
        }
    }

    val db = remember { SynapseDatabase.getInstance(context) }

    val sessionStatus by remember { SupabaseClientProvider.client.auth.sessionStatus }
        .collectAsState(initial = SupabaseClientProvider.client.auth.sessionStatus.value)

    val currentUserId = when (val s = sessionStatus) {
        is SessionStatus.Authenticated -> s.session.user?.id ?: ""
        else                           -> AuthManager.currentUserId ?: ""
    }

    val energyAllowed by EnergyQuotaRepository.energyAllowed.collectAsState()
    val energyUsed    by EnergyQuotaRepository.energyUsed.collectAsState()

    LaunchedEffect(lifecycleOwner, currentUserId) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            refreshKeyboardStatus()
            if (currentUserId.isNotEmpty()) {
                launch(Dispatchers.IO) {
                    EnergyQuotaRepository.syncEnergyFromRemote(context, currentUserId)
                }
            }
        }
    }

    val pendingRequests = remember { mutableIntStateOf(0) }
    val promptsToday    by EnergyQuotaRepository.promptsToday.collectAsState()
    val energyToday     by EnergyQuotaRepository.energyToday.collectAsState()
    val totalPrompts    by EnergyQuotaRepository.totalPrompts.collectAsState()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(HS_Bg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
    ) {
        val isWide = maxWidth >= 600.dp
        val hPad: Dp = if (isWide) 32.dp else 20.dp

        Column(modifier = Modifier.padding(horizontal = hPad)) {
            Spacer(modifier = Modifier.height(28.dp))

            // ── Header ──────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { -24 }
            ) {
                Column {
                    // Gradient accent line above heading
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(HS_Violet, HS_Violet.copy(alpha = 0.0f))
                                )
                            )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text       = "Dashboard",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = HS_Text
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text  = "Your AI keyboard at a glance",
                        fontSize = 14.sp,
                        color = HS_Muted
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Keyboard Status ──────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(500, 50)) + slideInVertically(tween(500, 50)) { 40 }
            ) {
                KeyboardStatusCard(
                    isEnabled         = isKeyboardEnabled.value,
                    isDefault         = isKeyboardDefault.value,
                    onEnableClick     = {
                        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                    },
                    onSetDefaultClick = {
                        context.getSystemService(InputMethodManager::class.java)
                            ?.showInputMethodPicker()
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── AI Energy Quota ──────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(500, 100)) + slideInVertically(tween(500, 100)) { 40 }
            ) {
                EnergyQuotaCard(energyUsed = energyUsed, energyAllowed = energyAllowed)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Offline Sync ─────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(500, 150)) + slideInVertically(tween(500, 150)) { 40 }
            ) {
                OfflineSyncCard(pendingRequests = pendingRequests.intValue)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── Section label ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(400, 200))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text          = "QUICK STATS",
                        fontSize      = 12.sp,
                        color         = HS_Muted,
                        letterSpacing = 1.5.sp,
                        fontWeight    = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Quick Stats ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter   = fadeIn(tween(500, 200)) + slideInVertically(tween(500, 200)) { 40 }
            ) {
                if (isWide) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatTile("Prompts Today", "$promptsToday", Icons.Rounded.RocketLaunch, Color(0xFF3B82F6), Modifier.weight(1f))
                        StatTile("Energy Today",  "$energyToday",  Icons.Rounded.Bolt,         HS_Success,        Modifier.weight(1f))
                        StatTile("Total Prompts", "$totalPrompts", Icons.Rounded.Functions,    Color(0xFFEC4899), Modifier.weight(1f))
                    }
                } else {
                    QuickStatsRow(promptsToday, energyToday, totalPrompts)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Keyboard Status Card
// ---------------------------------------------------------------------------
@Composable
private fun KeyboardStatusCard(
    isEnabled: Boolean,
    isDefault: Boolean,
    onEnableClick: () -> Unit,
    onSetDefaultClick: () -> Unit
) {
    val isFullyActive = isEnabled && isDefault
    val statusColor = when {
        isFullyActive -> HS_Success
        isEnabled     -> Color(0xFFFBBF24)
        else          -> Color(0xFFF87171)
    }
    val statusLabel = when {
        isFullyActive -> "Active & Running"
        isEnabled     -> "Enabled — Set as Default"
        else          -> "Not Enabled"
    }

    PremiumCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(1.dp, statusColor.copy(alpha = 0.28f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (isFullyActive) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                    contentDescription = null,
                    tint               = statusColor,
                    modifier           = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text          = "KEYBOARD STATUS",
                    fontSize      = 12.sp,
                    color         = HS_Muted,
                    letterSpacing = 1.0.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text       = statusLabel,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = statusColor
                )
            }
            // Live indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor)
                    .shadow(4.dp, RoundedCornerShape(4.dp), spotColor = statusColor)
            )
        }

        if (!isEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text     = "Go to Manage Keyboards and toggle \"Synapse Keyboard\" ON.",
                style    = MaterialTheme.typography.bodySmall,
                color    = HS_Muted,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Button(
                onClick  = onEnableClick,
                colors   = ButtonDefaults.buttonColors(containerColor = HS_Violet),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Text("Open Manage Keyboards", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, Modifier.size(16.dp))
            }
        }

        if (isEnabled && !isDefault) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text     = "Synapse is enabled. Now set it as your default keyboard.",
                style    = MaterialTheme.typography.bodySmall,
                color    = HS_Muted,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Button(
                onClick  = onSetDefaultClick,
                colors   = ButtonDefaults.buttonColors(containerColor = HS_Success),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Text("Set as Default", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.CheckCircle, null, Modifier.size(16.dp), tint = Color.White)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// AI Energy Quota Card
// ---------------------------------------------------------------------------
@Composable
private fun EnergyQuotaCard(energyUsed: Int, energyAllowed: Int) {
    val energyRemaining = (energyAllowed - energyUsed).coerceAtLeast(0)
    val fraction        = if (energyAllowed > 0) energyUsed.toFloat() / energyAllowed.toFloat() else 0f

    val animatedFraction by animateFloatAsState(
        targetValue   = fraction.coerceIn(0f, 1f),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label         = "energyGauge"
    )

    val gaugeColor = when {
        fraction >= 0.5f -> HS_Success
        fraction >= 0.2f -> Color(0xFFFBBF24)
        else             -> Color(0xFFF87171)
    }
    val statusLabel = when {
        energyRemaining <= 0  -> "Out of Energy"
        energyRemaining < 500 -> "Running Low"
        else                  -> "Energy Available"
    }

    PremiumCard {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Circular arc gauge
            Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(96.dp)) {
                    val stroke = 9.dp.toPx()
                    val inset  = stroke / 2f
                    val arcSz  = Size(size.width - stroke, size.height - stroke)
                    drawArc(
                        color      = gaugeColor.copy(alpha = 0.12f),
                        startAngle = 135f, sweepAngle = 270f, useCenter = false,
                        topLeft    = Offset(inset, inset), size = arcSz,
                        style      = Stroke(stroke, cap = StrokeCap.Round)
                    )
                    if (animatedFraction > 0f) {
                        drawArc(
                            brush      = Brush.sweepGradient(listOf(gaugeColor.copy(0.6f), gaugeColor, gaugeColor)),
                            startAngle = 135f, sweepAngle = 270f * animatedFraction, useCenter = false,
                            topLeft    = Offset(inset, inset), size = arcSz,
                            style      = Stroke(stroke, cap = StrokeCap.Round)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$energyRemaining", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = gaugeColor)
                    Text("left", fontSize = 9.sp, color = HS_Muted)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("AI ENERGY", fontSize = 12.sp, color = HS_Muted, letterSpacing = 1.0.sp)
                Spacer(Modifier.height(3.dp))
                Text(text = statusLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = gaugeColor)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier          = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(gaugeColor.copy(alpha = 0.10f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(gaugeColor))
                    Text("$energyUsed / $energyAllowed used", style = MaterialTheme.typography.bodySmall, color = gaugeColor.copy(0.85f))
                }
                Spacer(Modifier.height(5.dp))
                Text("Pay-as-you-go credit", style = MaterialTheme.typography.labelSmall, color = HS_Muted.copy(0.45f))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Offline Sync Card
// ---------------------------------------------------------------------------
@Composable
private fun OfflineSyncCard(pendingRequests: Int) {
    val isSynced  = pendingRequests == 0
    val syncColor = if (isSynced) HS_Success else Color(0xFFFBBF24)
    PremiumCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(syncColor.copy(alpha = 0.12f))
                    .border(1.dp, syncColor.copy(alpha = 0.22f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (isSynced) Icons.Rounded.CloudDone else Icons.Rounded.CloudSync,
                    contentDescription = null, tint = syncColor, modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text("SYNC STATUS", fontSize = 12.sp, color = HS_Muted, letterSpacing = 1.0.sp)
                Spacer(Modifier.height(3.dp))
                Text(
                    text       = if (isSynced) "All synced" else "$pendingRequests requests pending",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = syncColor
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(syncColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text       = if (isSynced) "Live" else "Pending",
                    style      = MaterialTheme.typography.labelSmall,
                    color      = syncColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Quick Stats — portrait layout
// ---------------------------------------------------------------------------
@Composable
private fun QuickStatsRow(promptsToday: Int, energyToday: Int, totalPrompts: Int) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Prompts Today", "$promptsToday", Icons.Rounded.RocketLaunch, Color(0xFF3B82F6), Modifier.weight(1f))
            StatTile("Energy Today",  "$energyToday",  Icons.Rounded.Bolt,         HS_Success,        Modifier.weight(1f))
        }
        StatTile("Total Prompts", "$totalPrompts", Icons.Rounded.Functions, Color(0xFFEC4899), Modifier.fillMaxWidth())
    }
}

// ---------------------------------------------------------------------------
// Stat Tile
// ---------------------------------------------------------------------------
@Composable
private fun StatTile(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(0.5.dp, accentColor.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 12.sp, color = HS_Muted)
        }
    }
}

// ---------------------------------------------------------------------------
// Premium solid card — replaces GlassCard
// ---------------------------------------------------------------------------
@Composable
private fun PremiumCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HS_Surface)
            .border(0.5.dp, HS_Border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content
    )
}
