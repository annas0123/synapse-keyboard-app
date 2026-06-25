package com.smafty.synapsekeyboard.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.ui.theme.ThemeManager

// ── Monochrome design tokens — all values resolve to ThemeManager (single source of truth).
private val AppBg: Color        get() = ThemeManager.currentTheme.background
private val SurfaceBg: Color    get() = ThemeManager.currentTheme.surface
private val BorderColor: Color  get() = ThemeManager.currentTheme.border
private val PrimaryMuted: Color get() = ThemeManager.currentTheme.primaryMuted
private val TextActive: Color   get() = ThemeManager.currentTheme.textPrimary
private val TextInactive: Color get() = ThemeManager.currentTheme.textTertiary

private data class NavItem(val icon: ImageVector, val label: String)
private val navItems = listOf(
    NavItem(Icons.Rounded.Home,     "Home"),
    NavItem(Icons.Rounded.Star,     "Prompts"),
    NavItem(Icons.Rounded.Settings, "Settings")
)

/**
 * MainDashboard — Phase 3 redesign.
 * - Solid AppBg (#0A0A0F) — NO aurora/blur/glassmorphism
 * - Bottom nav: solid SurfaceBg, top 0.5dp border, violet pill indicator
 * - Instant tab switching (no animation)
 * - 64dp nav bar height
 */
@Composable
fun MainDashboard(
    onNavigateToKeyboardTest: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showKeySoundsScreen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        // Tab content area — leaves room for 64dp nav bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            if (showKeySoundsScreen) {
                KeySoundsScreen(onBack = { showKeySoundsScreen = false })
            } else {
                when (selectedTab) {
                    0 -> HomeScreen()
                    1 -> PromptsScreen()
                    2 -> SettingsScreen(
                        onNavigateToKeyboardTest = onNavigateToKeyboardTest,
                        onSignOut = onSignOut,
                        onNavigateToKeySounds = { showKeySoundsScreen = true }
                    )
                }
            }
        }

        // Premium Minimal bottom nav bar
        PremiumNavBar(
            selectedTab   = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier      = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

/**
 * PremiumNavBar — solid, no blur, no shadow glow.
 * - 64dp height
 * - SurfaceBg background with 0.5dp top border
 * - Active tab: violet pill (#7C5CFC 15% opacity bg), icon + label visible
 * - Inactive: icon only, #555570 tint
 */
@Composable
private fun PremiumNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(SurfaceBg)
            // 0.5dp top border per spec
            .border(
                width = 0.5.dp,
                color = BorderColor,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedTab == index

            val iconTint by animateColorAsState(
                targetValue   = if (isSelected) TextActive else TextInactive,
                animationSpec = tween(150),
                label         = "navIcon$index"
            )
            val labelColor by animateColorAsState(
                targetValue   = if (isSelected) TextActive else TextInactive,
                animationSpec = tween(150),
                label         = "navLabel$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    // Violet pill bg only on active tab (15% opacity, 32dp height implied by padding)
                    .background(
                        if (isSelected) PrimaryMuted else Color.Transparent
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = { onTabSelected(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        tint               = iconTint,
                        modifier           = Modifier.size(22.dp)
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text       = item.label,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color      = labelColor
                        )
                    }
                }
            }
        }
    }
}
