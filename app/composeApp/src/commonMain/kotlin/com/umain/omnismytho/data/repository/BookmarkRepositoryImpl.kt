package com.umain.omnismytho.data.repository

import com.umain.omnismytho.data.local.BookmarkDao
import com.umain.omnismytho.data.local.BookmarkEntity
import com.umain.omnismytho.domain.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Room-backed bookmark repository. Persists across app restarts.
 * Exposes a StateFlow that stays in sync with the database.
 */
class BookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao,
) : BookmarkRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val _bookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    override val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    init {
        // Observe database changes and sync to StateFlow
        scope.launch {
            bookmarkDao.observeAll().collect { ids ->
                _bookmarkedIds.value = ids.toSet()
            }
        }
    }

    override fun isBookmarked(entityId: String): Boolean =
        entityId in _bookmarkedIds.value

    override fun toggle(entityId: String) {
        val wasBookmarked = isBookmarked(entityId)
        scope.launch {
            if (wasBookmarked) {
                bookmarkDao.delete(entityId)
            } else {
                bookmarkDao.insert(BookmarkEntity(entityId = entityId))
            }
        }
    }
}
