package com.forum.frontend.presentation.profile

import com.forum.frontend.domain.model.Badge
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserProfile

enum class ProfileTab(val label: String) {
    POSTS("Publicaciones"),
    BADGES("Insignias"),
}

data class ProfileUiState(
    val profile: UserProfile? = null,
    val badges: List<Badge> = emptyList(),
    val posts: List<Post> = emptyList(),
    val tab: ProfileTab = ProfileTab.POSTS,
    val isOwnProfile: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val page: Int = 0,
    val hasMore: Boolean = false,
)