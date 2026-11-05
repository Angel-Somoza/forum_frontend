package com.forum.frontend.presentation.notifications

import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.Notification
import com.forum.frontend.domain.repository.NotificationRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state.asStateFlow()

    val unreadCount: StateFlow<Int> = notificationRepository.unreadCount

    private var loadJob: Job? = null

    init {
        loadFirstPage()
    }

    fun retry() {
        loadFirstPage()
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        _state.update { it.copy(isLoadingMore = true) }
        loadJob = scope.launch {
            resultOf { notificationRepository.getNotifications(current.page + 1) }
                .onSuccess { result ->
                    _state.update { s ->
                        s.copy(
                            notifications = s.notifications + result.items.filter { new -> s.notifications.none { it.id == new.id } },
                            page = result.page,
                            hasMore = result.hasMore,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { error -> _state.update { it.copy(isLoadingMore = false, message = error.toUserMessage()) } }
        }
    }

    fun markAsRead(id: Int) {
        val target = _state.value.notifications.firstOrNull { it.id == id } ?: return
        if (target.isRead) return
        replace(target.copy(isRead = true))
        scope.launch {
            resultOf { notificationRepository.markRead(id) }
                .onFailure { replace(target) }
        }
    }

    fun markAllAsRead() {
        val snapshot = _state.value.notifications
        if (_state.value.isMarkingAll || snapshot.none { !it.isRead }) return
        _state.update { s -> s.copy(isMarkingAll = true, notifications = s.notifications.map { it.copy(isRead = true) }) }
        scope.launch {
            resultOf { notificationRepository.markAllRead() }
                .onSuccess { _state.update { it.copy(isMarkingAll = false) } }
                .onFailure { error ->
                    _state.update { it.copy(isMarkingAll = false, notifications = snapshot, message = error.toUserMessage()) }
                }
        }
    }

    fun delete(notification: Notification) {
        val snapshot = _state.value.notifications
        _state.update { s -> s.copy(notifications = s.notifications.filterNot { it.id == notification.id }) }
        scope.launch {
            resultOf { notificationRepository.delete(notification.id, !notification.isRead) }
                .onFailure { error -> _state.update { it.copy(notifications = snapshot, message = error.toUserMessage()) } }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(message = null) }
    }

    private fun loadFirstPage() {
        loadJob?.cancel()
        _state.update { it.copy(isLoading = true, error = null, notifications = emptyList(), page = 0, hasMore = false) }
        loadJob = scope.launch {
            resultOf { notificationRepository.getNotifications(1) }
                .onSuccess { result ->
                    _state.update {
                        it.copy(isLoading = false, notifications = result.items, page = result.page, hasMore = result.hasMore)
                    }
                    resultOf { notificationRepository.refreshUnreadCount() }
                }
                .onFailure { error -> _state.update { it.copy(isLoading = false, error = error.toUserMessage()) } }
        }
    }

    private fun replace(notification: Notification) {
        _state.update { s -> s.copy(notifications = s.notifications.map { if (it.id == notification.id) notification else it }) }
    }
}
