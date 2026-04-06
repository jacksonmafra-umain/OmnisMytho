package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.EntityType
import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.revolver.RevolverViewModel

class CatalogViewModel(
    private val mythologyId: String,
    private val entityRepository: EntityRepository,
) : RevolverViewModel<CatalogEvent, CatalogState, CatalogEffect>(
    initialState = CatalogState.Loading
) {
    private var currentFilter: EntityType? = null

    init {
        addEventHandler<CatalogEvent.LoadEntities> { _, emit ->
            emit.state(CatalogState.Loading)
            loadEntities(emit, page = 1)
        }

        addEventHandler<CatalogEvent.OnFilterChanged> { event, emit ->
            currentFilter = event.type
            emit.state(CatalogState.Loading)
            loadEntities(emit, page = 1)
        }

        addEventHandler<CatalogEvent.OnEntityClicked> { event, emit ->
            emit.effect(CatalogEffect.NavigateToDetail(event.entityId))
        }

        addEventHandler<CatalogEvent.LoadNextPage> { _, emit ->
            val current = state.value
            if (current is CatalogState.Loaded && current.hasMore) {
                loadEntities(emit, page = current.currentPage + 1, appendTo = current)
            }
        }
    }

    private suspend fun loadEntities(
        emit: Emit,
        page: Int,
        appendTo: CatalogState.Loaded? = null,
    ) {
        try {
            val result = entityRepository.getEntities(
                mythologyId = mythologyId,
                type = currentFilter,
                page = page,
            )
            val entities = if (appendTo != null) {
                appendTo.entities + result.items
            } else {
                result.items
            }
            emit.state(
                CatalogState.Loaded(
                    entities = entities,
                    currentFilter = currentFilter,
                    hasMore = page < result.totalPages,
                    currentPage = page,
                )
            )
        } catch (e: Exception) {
            emit.state(CatalogState.Error(e.message ?: "Failed to load entities"))
        }
    }
}
