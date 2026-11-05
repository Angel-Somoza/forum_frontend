package com.forum.frontend.presentation.search

import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserSummary

enum class SearchTab(val label: String) {
    ALL("Todo"),
    PEOPLE("Personas"),
    POSTS("Publicaciones"),
}

data class SearchUiState(
    val query: String = "",
    val tab: SearchTab = SearchTab.ALL,
    val posts: List<Post> = emptyList(),
    val people: List<UserSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val hasMore: Boolean = false,
    val hasSearched: Boolean = false,
) {
    val isQueryTooShort: Boolean get() = query.trim().length < MIN_QUERY_LENGTH

    val isEmptyResult: Boolean
        get() = hasSearched && !isLoading && error == null && when (tab) {
            SearchTab.ALL -> posts.isEmpty() && people.isEmpty()
            SearchTab.POSTS -> posts.isEmpty()
            SearchTab.PEOPLE -> people.isEmpty()
        }

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}
