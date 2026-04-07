package com.umain.omnismytho.presentation.ui.organism

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

enum class NavTab(
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
) {
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
    // Respect system navigation bar insets for edge-to-edge
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 21.dp, vertical = 12.dp)
                .navigationBarsPadding(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavTab.entries.forEach { tab ->
                val isActive = tab == currentTab
                val bgColor by animateColorAsState(
                    targetValue = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(200),
                )
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(26.dp))
                            .background(bgColor)
                            .clickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = if (isActive) tab.activeIcon else tab.inactiveIcon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(18.dp),
                            tint =
                                if (isActive) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        )
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color =
                                if (isActive) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
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
