package com.smafty.synapsekeyboard.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smafty.synapsekeyboard.ui.theme.DeepSlate
import com.smafty.synapsekeyboard.ui.theme.ElectricPurple
import com.smafty.synapsekeyboard.ui.theme.MutedGrey
import com.smafty.synapsekeyboard.ui.theme.PitchBlack
import com.smafty.synapsekeyboard.ui.theme.TextColor

// ---------------------------------------------------------------------------
// Navigation item data model
// ---------------------------------------------------------------------------
private data class NavItem(val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Icons.Rounded.Home,     "Home"),
    NavItem(Icons.Rounded.Star,     "Prompts"),
    NavItem(Icons.Rounded.Settings, "Settings")
)

// ---------------------------------------------------------------------------
// Main Dashboard host
// ---------------------------------------------------------------------------
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
            .background(DeepSlate)
    ) {
        // Ambient aurora glow — top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricPurple.copy(alpha = 0.12f),
                            Color(0xFF00D4FF).copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Tab content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp)
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

        // Frosted nav bar
        FrostedNavBar(
            selectedTab   = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier      = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Frosted-Glass Nav Bar with glowing spring-animated pill
// ---------------------------------------------------------------------------
@Composable
private fun FrostedNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 24.dp,
                shape        = RoundedCornerShape(24.dp),
                spotColor    = ElectricPurple.copy(alpha = 0.30f),
                ambientColor = ElectricPurple.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(PitchBlack.copy(alpha = 0.96f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.10f),
                        ElectricPurple.copy(alpha = 0.18f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(5.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val tabWidth = maxWidth / navItems.size

            // Spring-animated pill offset
            val pillOffset by animateDpAsState(
                targetValue   = tabWidth * selectedTab,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMedium
                ),
                label = "pillSlide"
            )

            // Halo behind active pill
            Box(
                modifier = Modifier
                    .offset(x = pillOffset)
                    .width(tabWidth)
                    .height(46.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ElectricPurple.copy(alpha = 0.18f))
            )

            // Active pill card
            Box(
                modifier = Modifier
                    .offset(x = pillOffset)
                    .width(tabWidth)
                    .height(46.dp)
                    .shadow(8.dp, RoundedCornerShape(18.dp),
                        spotColor = ElectricPurple.copy(alpha = 0.45f),
                        ambientColor = ElectricPurple.copy(alpha = 0.15f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DeepSlate.copy(alpha = 0.95f), Color(0xFF1E1A2E))
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricPurple.copy(alpha = 0.60f),
                                ElectricPurple.copy(alpha = 0.20f)
                            )
                        ),
                        RoundedCornerShape(18.dp)
                    )
            )

            // Tab items row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index

                    val iconTint by animateColorAsState(
                        targetValue   = if (isSelected) ElectricPurple else MutedGrey.copy(alpha = 0.50f),
                        animationSpec = tween(180),
                        label         = "icon$index"
                    )
                    val labelColor by animateColorAsState(
                        targetValue   = if (isSelected) TextColor else MutedGrey.copy(alpha = 0.40f),
                        animationSpec = tween(180),
                        label         = "label$index"
                    )

                    Column(
                        modifier = Modifier
                            .width(tabWidth)
                            .height(46.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                                onClick           = { onTabSelected(index) }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector        = item.icon,
                            contentDescription = item.label,
                            tint               = iconTint,
                            modifier           = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text          = item.label,
                                fontSize      = 10.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = labelColor,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
