package com.forum.frontend.presentation.notifications

import com.forum.frontend.domain.model.Notification

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isMarkingAll: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val page: Int = 0,
    val hasMore: Boolean = false,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && notifications.isEmpty()
}
