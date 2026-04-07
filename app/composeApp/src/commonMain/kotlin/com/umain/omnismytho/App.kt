package com.umain.omnismytho

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umain.omnismytho.presentation.navigation.AppNavGraph
import com.umain.omnismytho.presentation.navigation.Route
import com.umain.omnismytho.presentation.ui.organism.BottomNavBar
import com.umain.omnismytho.presentation.ui.organism.NavTab
import com.umain.omnismytho.presentation.ui.theme.OmnisMythoTheme

@Composable
fun App() {
    // Force dark mode to match grimoire design
    OmnisMythoTheme(darkTheme = true) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()

        // Determine current tab from route
        val currentRoute = backStackEntry?.destination?.route ?: ""
        val currentTab = when {
            currentRoute.contains("Home") -> NavTab.HOME
            currentRoute.contains("Catalog") -> NavTab.CATALOG
            currentRoute.contains("Search") -> NavTab.SEARCH
            else -> NavTab.HOME
        }

        // Show bottom nav on main screens only (not splash, not detail)
        val showBottomNav = currentRoute.contains("Home") ||
            currentRoute.contains("Catalog") ||
            currentRoute.contains("Search")

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomNav) {
                    BottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            when (tab) {
                                NavTab.HOME -> navController.navigate(Route.Home) {
                                    popUpTo(Route.Home) { inclusive = true }
                                }
                                NavTab.CATALOG -> {} // TODO: navigate to catalog list
                                NavTab.SEARCH -> navController.navigate(Route.Search)
                                NavTab.SAVED -> {} // TODO: saved screen
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
                AppNavGraph(navController = navController)
            }
        }
    }
}
