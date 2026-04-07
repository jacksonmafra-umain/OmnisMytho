package com.umain.omnismytho.presentation.viewmodel

import com.umain.omnismytho.domain.repository.BookmarkRepository
import com.umain.omnismytho.domain.repository.EntityRepository
import com.umain.revolver.RevolverViewModel

class DetailViewModel(
    private val entityId: String,
    private val entityRepository: EntityRepository,
    private val bookmarkRepository: BookmarkRepository,
) : RevolverViewModel<DetailEvent, DetailState, DetailEffect>(
    initialState = DetailState.Loading,
) {
    init {
        addEventHandler<DetailEvent.LoadEntity> { _, emit ->
            emit.state(DetailState.Loading)
            try {
                val entity = entityRepository.getEntity(entityId)
                emit.state(
                    DetailState.Loaded(
                        entity = entity,
                        isBookmarked = bookmarkRepository.isBookmarked(entityId),
                    )
                )
            } catch (e: Exception) {
                emit.state(DetailState.Error(e.message ?: "Failed to load entity"))
            }
        }

        addEventHandler<DetailEvent.ToggleBookmark> { _, emit ->
            bookmarkRepository.toggle(entityId)
            val isNowBookmarked = bookmarkRepository.isBookmarked(entityId)

            // Update state to reflect new bookmark status
            val currentState = state.value
            if (currentState is DetailState.Loaded) {
                emit.state(currentState.copy(isBookmarked = isNowBookmarked))
            }

            val message = if (isNowBookmarked) "Saved to bookmarks" else "Removed from bookmarks"
            emit.effect(DetailEffect.ShowSnackbar(message))
        }
    }
}
