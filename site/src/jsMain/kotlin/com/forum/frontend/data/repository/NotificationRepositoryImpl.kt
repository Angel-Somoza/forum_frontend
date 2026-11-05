package com.forum.frontend.data.repository

import com.forum.frontend.data.mapper.toDomain
import com.forum.frontend.data.mapper.toPaged
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.MarkReadRequestDto
import com.forum.frontend.data.remote.dto.NotificationDto
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.UnreadCountDto
import com.forum.frontend.domain.model.Notification
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.repository.NotificationRepository
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationRepositoryImpl(
    private val api: ApiClient,
) : NotificationRepository {

    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    override suspend fun refreshUnreadCount() {
        _unreadCount.value = api.get<DataResponse<UnreadCountDto>>("/notifications/unread-count").data.count
    }

    override suspend fun getNotifications(page: Int): Paged<Notification> =
        api.get<PageResponse<NotificationDto>>("/notifications?page=$page&limit=$PAGE_SIZE").toPaged { it.toDomain() }

    override suspend fun markRead(id: Int) {
        api.sendBody(HttpMethod.Patch, "/notifications/$id", MarkReadRequestDto(true))
        decrementUnread()
    }

    override suspend fun markAllRead() {
        api.sendEmpty(HttpMethod.Patch, "/notifications/read-all")
        _unreadCount.value = 0
    }

    override suspend fun delete(id: Int, wasUnread: Boolean) {
        api.sendEmpty(HttpMethod.Delete, "/notifications/$id")
        if (wasUnread) decrementUnread()
    }

    private fun decrementUnread() {
        _unreadCount.update { (it - 1).coerceAtLeast(0) }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}