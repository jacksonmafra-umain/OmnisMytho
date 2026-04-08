package com.umain.omnismytho

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.umain.omnismytho.presentation.navigation.Route
import com.umain.omnismytho.presentation.navigation.appEntryProvider
import com.umain.omnismytho.presentation.ui.organism.BottomNavBar
import com.umain.omnismytho.presentation.ui.organism.NavTab
import com.umain.omnismytho.presentation.ui.theme.OmnisMythoTheme

@Composable
fun App() {
    OmnisMythoTheme(darkTheme = true) {
        val backStack = rememberNavBackStack(Route.Splash)

        // Derive current tab from the back stack top
        val currentKey by remember { derivedStateOf { backStack.lastOrNull() } }
        val currentTab by remember {
            derivedStateOf {
                when (currentKey) {
                    is Route.Home -> NavTab.HOME
                    is Route.CatalogAll, is Route.Catalog -> NavTab.CATALOG
                    is Route.Search -> NavTab.SEARCH
                    is Route.Saved -> NavTab.SAVED
                    else -> NavTab.HOME
                }
            }
        }

        val showBottomNav by remember {
            derivedStateOf {
                currentKey is Route.Home ||
                    currentKey is Route.CatalogAll ||
                    currentKey is Route.Catalog ||
                    currentKey is Route.Search ||
                    currentKey is Route.Saved
            }
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                if (showBottomNav) {
                    BottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            // Pop to Home first, then push the tab destination
                            val target: NavKey = when (tab) {
                                NavTab.HOME -> Route.Home
                                NavTab.CATALOG -> Route.CatalogAll
                                NavTab.SEARCH -> Route.Search
                                NavTab.SAVED -> Route.Saved
                            }
                            // Keep Home as root, replace everything above it
                            val homeIndex = backStack.indexOfFirst { it is Route.Home }
                            if (homeIndex >= 0) {
                                while (backStack.size > homeIndex + 1) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            }
                            if (target !is Route.Home) {
                                backStack.add(target)
                            }
                        },
                    )
                }
            },
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                NavDisplay(
                    backStack = backStack,
                    entryProvider = appEntryProvider(backStack),
                    onBack = {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    },
                )
            }
        }
    }
}
