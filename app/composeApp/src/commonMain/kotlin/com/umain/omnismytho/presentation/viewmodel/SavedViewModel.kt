package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.repository.BookmarkRepository
import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.revolver.RevolverViewModel

class SavedViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val entityRepository: EntityRepository,
) : RevolverViewModel<SavedEvent, SavedState, SavedEffect>(
    initialState = SavedState.Loading,
) {
    init {
        addEventHandler<SavedEvent.LoadSaved> { _, emit ->
            emit.state(SavedState.Loading)
            try {
                val ids = bookmarkRepository.bookmarkedIds.value
                if (ids.isEmpty()) {
                    emit.state(SavedState.Loaded(emptyList()))
                    return@addEventHandler
                }
                val entities = ids.mapNotNull { id ->
                    try {
                        entityRepository.getEntity(id)
                    } catch (_: Exception) {
                        null
                    }
                }
                emit.state(SavedState.Loaded(entities))
            } catch (e: Exception) {
                emit.state(SavedState.Error(e.message ?: "Failed to load saved"))
            }
        }

        addEventHandler<SavedEvent.OnEntityClicked> { event, emit ->
            emit.effect(SavedEffect.NavigateToDetail(event.entityId))
        }
    }
}
