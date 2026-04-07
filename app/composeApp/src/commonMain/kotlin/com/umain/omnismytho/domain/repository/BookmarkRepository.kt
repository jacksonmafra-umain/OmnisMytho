package com.umain.omnismytho.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface BookmarkRepository {
    val bookmarkedIds: StateFlow<Set<String>>
    fun isBookmarked(entityId: String): Boolean
    fun toggle(entityId: String)
}
