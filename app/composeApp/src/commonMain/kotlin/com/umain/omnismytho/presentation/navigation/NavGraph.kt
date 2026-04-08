package com.umain.omnismytho.presentation.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.umain.omnismytho.presentation.ui.page.*

/**
 * Navigation 3 entry provider — maps Route keys to Page composables.
 * The backStack is managed externally in App.kt.
 */
fun appEntryProvider(
    backStack: MutableList<NavKey>,
): (NavKey) -> NavEntry<NavKey> = entryProvider {

    entry<Route.Splash> {
        SplashPage(
            onNavigateToHome = {
                backStack.clear()
                backStack.add(Route.Home)
            },
        )
    }

    entry<Route.Home> {
        HomePage(
            onNavigateToCatalog = { mythologyId ->
                backStack.add(Route.Catalog(mythologyId))
            },
            onNavigateToSearch = {
                backStack.add(Route.Search)
            },
        )
    }

    entry<Route.Catalog> { key ->
        CatalogPage(
            onNavigateToDetail = { entityId ->
                backStack.add(Route.Detail(entityId))
            },
            onNavigateBack = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            },
            mythologyId = key.mythologyId,
        )
    }

    entry<Route.Detail> { key ->
        DetailPage(
            onNavigateBack = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            },
            entityId = key.entityId,
        )
    }

    entry<Route.CatalogAll> {
        CatalogAllPage(
            onNavigateToDetail = { entityId ->
                backStack.add(Route.Detail(entityId))
            },
        )
    }

    entry<Route.Search> {
        SearchPage(
            onNavigateToDetail = { entityId ->
                backStack.add(Route.Detail(entityId))
            },
            onNavigateBack = {
                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            },
        )
    }

    entry<Route.Saved> {
        SavedPage(
            onNavigateToDetail = { entityId ->
                backStack.add(Route.Detail(entityId))
            },
        )
    }
}
