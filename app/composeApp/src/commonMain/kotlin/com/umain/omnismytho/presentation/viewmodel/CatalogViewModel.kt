package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.model.EntityType
import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.revolver.RevolverViewModel

class CatalogViewModel(
    private val mythologyId: String,
    private val entityRepository: EntityRepository,
) : RevolverViewModel<CatalogEvent, CatalogState, CatalogEffect>(
    initialState = CatalogState.Loading,
) {
    private var currentFilter: EntityType? = null
    private var currentPage: Int = 1
    private var currentEntities: List<com.umain.omnismytho.domain.model.Entity> = emptyList()
    private var hasMore: Boolean = false

    init {
        addEventHandler<CatalogEvent.LoadEntities> { _, emit ->
            emit.state(CatalogState.Loading)
            currentPage = 1
            currentEntities = emptyList()
            try {
                val result = entityRepository.getEntities(
                    mythologyId = mythologyId,
                    type = currentFilter,
                    page = 1,
                )
                currentEntities = result.items
                hasMore = 1 < result.totalPages
                currentPage = 1
                emit.state(
                    CatalogState.Loaded(
                        entities = currentEntities,
                        currentFilter = currentFilter,
                        hasMore = hasMore,
                        currentPage = currentPage,
                    ),
                )
            } catch (e: Exception) {
                emit.state(CatalogState.Error(e.message ?: "Failed to load entities"))
            }
        }

        addEventHandler<CatalogEvent.OnFilterChanged> { event, emit ->
            currentFilter = event.type
            emit.state(CatalogState.Loading)
            currentPage = 1
            currentEntities = emptyList()
            try {
                val result = entityRepository.getEntities(
                    mythologyId = mythologyId,
                    type = currentFilter,
                    page = 1,
                )
                currentEntities = result.items
                hasMore = 1 < result.totalPages
                currentPage = 1
                emit.state(
                    CatalogState.Loaded(
                        entities = currentEntities,
                        currentFilter = currentFilter,
                        hasMore = hasMore,
                        currentPage = currentPage,
                    ),
                )
            } catch (e: Exception) {
                emit.state(CatalogState.Error(e.message ?: "Failed to load entities"))
            }
        }

        addEventHandler<CatalogEvent.OnEntityClicked> { event, emit ->
            emit.effect(CatalogEffect.NavigateToDetail(event.entityId))
        }

        addEventHandler<CatalogEvent.LoadNextPage> { _, emit ->
            if (hasMore) {
                val nextPage = currentPage + 1
                try {
                    val result = entityRepository.getEntities(
                        mythologyId = mythologyId,
                        type = currentFilter,
                        page = nextPage,
                    )
                    currentEntities = currentEntities + result.items
                    hasMore = nextPage < result.totalPages
                    currentPage = nextPage
                    emit.state(
                        CatalogState.Loaded(
                            entities = currentEntities,
                            currentFilter = currentFilter,
                            hasMore = hasMore,
                            currentPage = currentPage,
                        ),
                    )
                } catch (e: Exception) {
                    emit.state(CatalogState.Error(e.message ?: "Failed to load more"))
                }
            }
        }
    }
}
