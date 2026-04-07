package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.revolver.RevolverViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class SearchViewModel(
    private val entityRepository: EntityRepository,
) : RevolverViewModel<SearchEvent, SearchState, SearchEffect>(
        initialState = SearchState.Idle,
    ) {
    private var searchJob: Job? = null

    init {
        addEventHandler<SearchEvent.OnQueryChanged> { event, emit ->
            val query = event.query.trim()
            if (query.length < 2) {
                emit.state(SearchState.Idle)
                return@addEventHandler
            }
            emit.state(SearchState.Searching)
            // Debounce: wait 400ms before searching
            delay(400)
            try {
                val results = entityRepository.searchEntities(query)
                if (results.isEmpty()) {
                    emit.state(SearchState.Empty(query))
                } else {
                    emit.state(SearchState.Results(results, query))
                }
            } catch (e: Exception) {
                emit.state(SearchState.Error(e.message ?: "Search failed"))
            }
        }

        addEventHandler<SearchEvent.OnEntityClicked> { event, emit ->
            emit.effect(SearchEffect.NavigateToDetail(event.entityId))
        }

        addEventHandler<SearchEvent.OnClear> { _, emit ->
            emit.state(SearchState.Idle)
        }
    }
}
