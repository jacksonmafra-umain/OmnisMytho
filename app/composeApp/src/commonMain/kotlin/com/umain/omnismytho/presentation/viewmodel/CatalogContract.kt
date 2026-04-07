package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.Entity
import com.umain.omnismytho.domain.model.EntityType
import com.umain.omnismytho.domain.model.Mythology
import com.umain.revolver.RevolverEffect
import com.umain.revolver.RevolverEvent
import com.umain.revolver.RevolverState

sealed interface CatalogEvent : RevolverEvent {
    data object LoadEntities : CatalogEvent

    data class OnFilterChanged(
        val type: EntityType?,
    ) : CatalogEvent

    data class OnEntityClicked(
        val entityId: String,
    ) : CatalogEvent

    data object LoadNextPage : CatalogEvent

    data object OnToggleSort : CatalogEvent
}

sealed interface CatalogState : RevolverState {
    data object Loading : CatalogState

    data class Loaded(
        val entities: List<Entity>,
        val mythology: Mythology? = null,
        val currentFilter: EntityType? = null,
        val hasMore: Boolean = false,
        val currentPage: Int = 1,
        val sortAscending: Boolean = true,
    ) : CatalogState

    data class Error(
        val message: String,
    ) : CatalogState
}

sealed interface CatalogEffect : RevolverEffect {
    data class NavigateToDetail(
        val entityId: String,
    ) : CatalogEffect
}
