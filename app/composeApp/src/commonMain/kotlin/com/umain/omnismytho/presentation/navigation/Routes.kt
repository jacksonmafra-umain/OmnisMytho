package com.umain.omnismytho.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object CatalogAll : Route

    @Serializable
    data class Catalog(val mythologyId: String) : Route

    @Serializable
    data object Saved : Route

    @Serializable
    data class Detail(val entityId: String) : Route

    @Serializable
    data object Search : Route
}
