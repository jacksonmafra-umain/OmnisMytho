package com.umain.omnismytho.data.repository

import com.umain.omnismytho.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory bookmark storage. Replace with Room/SQLDelight for persistence.
 * Registered as singleton in Koin so state survives across screens.
 */
class BookmarkRepositoryImpl : BookmarkRepository {

    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    override val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    override fun isBookmarked(entityId: String): Boolean =
        entityId in _bookmarkedIds.value

    override fun toggle(entityId: String) {
        _bookmarkedIds.value = if (entityId in _bookmarkedIds.value) {
            _bookmarkedIds.value - entityId
        } else {
            _bookmarkedIds.value + entityId
        }
    }
}
