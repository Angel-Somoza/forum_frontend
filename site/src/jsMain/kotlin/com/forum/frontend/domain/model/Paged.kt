package com.forum.frontend.domain.model

data class Paged<T>(
    val items: List<T>,
    val page: Int,
    val totalPages: Int,
) {
    val hasMore: Boolean get() = page < totalPages
}