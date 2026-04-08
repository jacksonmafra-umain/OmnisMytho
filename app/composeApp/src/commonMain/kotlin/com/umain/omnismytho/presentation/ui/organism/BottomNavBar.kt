package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

enum class NavTab(val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    HOME("HOME", Icons.Filled.Home, Icons.Outlined.Home),
    CATALOG("CATALOG", Icons.Outlined.GridView, Icons.Outlined.GridView),
    SEARCH("SEARCH", Icons.Filled.Search, Icons.Outlined.Search),
    SAVED("SAVED", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
}

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = NavTab.entries.indexOf(currentTab)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp, vertical = 12.dp)
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavTab.entries.forEachIndexed { index, tab ->
                val isActive = index == selectedIndex

                // Spring-based icon lift
                val iconLift by animateFloatAsState(
                    targetValue = if (isActive) -2f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                    label = "iconLift",
                )

                // Scale pop on selection
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.05f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
                    label = "scale",
                )

                val bgColor by animateColorAsState(
                    targetValue = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(250),
                    label = "bgColor",
                )

                val contentColor by animateColorAsState(
                    targetValue = if (isActive) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(250),
                    label = "contentColor",
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(26.dp))
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onTabSelected(tab) }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = if (isActive) tab.activeIcon else tab.inactiveIcon,
                            contentDescription = tab.label,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer { translationY = iconLift },
                            tint = contentColor,
                        )
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color = contentColor,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BottomNavBarHomePreview() {
    OmPreviewSurface {
        BottomNavBar(currentTab = NavTab.HOME, onTabSelected = {})
    }
}

@Preview
@Composable
private fun BottomNavBarSearchPreview() {
    OmPreviewSurface {
        BottomNavBar(currentTab = NavTab.SEARCH, onTabSelected = {})
    }
}
