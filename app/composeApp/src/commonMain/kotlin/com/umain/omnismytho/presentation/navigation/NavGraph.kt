package com.umain.omnismytho.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.umain.omnismytho.presentation.ui.page.CatalogAllPage
import com.umain.omnismytho.presentation.ui.page.CatalogPage
import com.umain.omnismytho.presentation.ui.page.DetailPage
import com.umain.omnismytho.presentation.ui.page.HomePage
import com.umain.omnismytho.presentation.ui.page.SavedPage
import com.umain.omnismytho.presentation.ui.page.SearchPage
import com.umain.omnismytho.presentation.ui.page.SplashPage

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(300)) { it / 4 } },
    ) {
        composable<Route.Splash>(
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(300)) },
        ) {
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
        composable<Route.CatalogAll> {
            CatalogAllPage(
                onNavigateToDetail = { entityId ->
                    navController.navigate(Route.Detail(entityId))
                },
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
        composable<Route.Saved> {
            SavedPage()
        }
    }
}
