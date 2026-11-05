package com.forum.frontend.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    val unreadCount: StateFlow<Int>
    suspend fun refreshUnreadCount()
}