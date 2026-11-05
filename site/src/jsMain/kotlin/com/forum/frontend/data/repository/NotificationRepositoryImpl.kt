package com.forum.frontend.data.repository

import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.UnreadCountDto
import com.forum.frontend.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepositoryImpl(
    private val api: ApiClient,
) : NotificationRepository {

    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    override suspend fun refreshUnreadCount() {
        _unreadCount.value = api.get<DataResponse<UnreadCountDto>>("/notifications/unread-count").data.count
    }
}