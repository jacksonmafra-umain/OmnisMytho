package com.umain.omnismytho.domain.model

data class PaginatedResult<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
)
