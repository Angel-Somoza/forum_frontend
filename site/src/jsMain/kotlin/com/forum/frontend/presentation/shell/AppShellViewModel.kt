package com.forum.frontend.presentation.shell

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.NotificationRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppShellViewModel(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val loading: LoadingTracker,
) : ViewModel() {

    val currentUser: StateFlow<CurrentUser?> = authRepository.currentUser
    val unreadCount: StateFlow<Int> = notificationRepository.unreadCount

    fun refreshUnreadCount() {
        scope.launch {
            resultOf { notificationRepository.refreshUnreadCount() }
        }
    }

    fun logout() {
        scope.launch {
            resultOf { loading.blocking("Cerrando sesión") { authRepository.logout() } }
        }
    }
}