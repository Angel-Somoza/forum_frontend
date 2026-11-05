package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.Notification
import com.forum.frontend.domain.model.Paged
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    val unreadCount: StateFlow<Int>
    suspend fun refreshUnreadCount()
    suspend fun getNotifications(page: Int): Paged<Notification>
    suspend fun markRead(id: Int)
    suspend fun markAllRead()
    suspend fun delete(id: Int, wasUnread: Boolean)
}