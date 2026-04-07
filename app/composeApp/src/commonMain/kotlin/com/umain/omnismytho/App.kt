package com.umain.omnismytho

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.umain.omnismytho.presentation.navigation.AppNavGraph
import com.umain.omnismytho.presentation.navigation.Route
import com.umain.omnismytho.presentation.ui.organism.BottomNavBar
import com.umain.omnismytho.presentation.ui.organism.NavTab
import com.umain.omnismytho.presentation.ui.theme.OmnisMythoTheme

@Composable
fun App() {
    OmnisMythoTheme(darkTheme = true) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()

        val currentRoute = backStackEntry?.destination?.route ?: ""
        val currentTab =
            when {
                currentRoute.contains("Home") -> NavTab.HOME
                currentRoute.contains("Catalog") -> NavTab.CATALOG
                currentRoute.contains("Search") -> NavTab.SEARCH
                currentRoute.contains("Saved") -> NavTab.SAVED
                else -> NavTab.HOME
            }

        val showBottomNav =
            currentRoute.contains("Home") ||
                currentRoute.contains("Catalog") ||
                currentRoute.contains("Search") ||
                currentRoute.contains("Saved")

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            // Let each screen handle its own insets for true edge-to-edge
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                if (showBottomNav) {
                    BottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            when (tab) {
                                NavTab.HOME -> {
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.Home) { inclusive = true }
                                    }
                                }

                                NavTab.CATALOG -> {
                                    navController.navigate(Route.CatalogAll) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }

                                NavTab.SEARCH -> {
                                    navController.navigate(Route.Search) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }

                                NavTab.SAVED -> {
                                    navController.navigate(Route.Saved) {
                                        popUpTo(Route.Home) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                    )
                }
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                AppNavGraph(navController = navController)
            }
        }
    }
}
