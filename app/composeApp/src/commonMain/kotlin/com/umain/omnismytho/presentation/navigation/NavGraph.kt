package com.umain.omnismytho.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.umain.omnismytho.presentation.ui.page.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash,
    ) {
        composable<Route.Splash> {
            SplashPage(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Home> {
            HomePage(
                onNavigateToCatalog = { mythologyId ->
                    navController.navigate(Route.Catalog(mythologyId))
                },
                onNavigateToSearch = {
                    navController.navigate(Route.Search)
                },
            )
        }
        composable<Route.Catalog> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Catalog>()
            CatalogPage(
                onNavigateToDetail = { entityId ->
                    navController.navigate(Route.Detail(entityId))
                },
                onNavigateBack = { navController.popBackStack() },
                mythologyId = route.mythologyId,
            )
        }
        composable<Route.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Detail>()
            DetailPage(
                onNavigateBack = { navController.popBackStack() },
                entityId = route.entityId,
            )
        }
        composable<Route.Search> {
            SearchPage(
                onNavigateToDetail = { entityId ->
                    navController.navigate(Route.Detail(entityId))
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
