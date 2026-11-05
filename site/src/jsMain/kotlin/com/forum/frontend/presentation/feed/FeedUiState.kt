package com.forum.frontend.presentation.feed

import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.Space

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val page: Int = 0,
    val hasMore: Boolean = false,
    val spaces: List<Space> = emptyList(),
    val selectedSpaceId: Int? = null,
    val showSpaceFilter: Boolean = false,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && posts.isEmpty()
}