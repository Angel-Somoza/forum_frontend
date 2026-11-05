package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.domain.model.NotificationType
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.notifications.NotificationsScreen
import com.forum.frontend.presentation.notifications.NotificationsViewModel
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun NotificationsPage() {
    val ctx = rememberPageContext()
    val viewModel = rememberViewModel<NotificationsViewModel>()
    val state by viewModel.state.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) { document.title = "Notificaciones · Forum" }

    NotificationsScreen(
        state = state,
        unreadCount = unreadCount,
        onOpen = { notification ->
            viewModel.markAsRead(notification.id)
            val target = when {
                notification.postId != null -> "/post?id=${notification.postId}"
                notification.type == NotificationType.BADGE -> "/profile"
                else -> null
            }
            if (target != null) ctx.router.navigateTo(target)
        },
        onMarkAllClick = viewModel::markAllAsRead,
        onDeleteClick = viewModel::delete,
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadMore,
        onDismissMessage = viewModel::dismissMessage,
    )
}