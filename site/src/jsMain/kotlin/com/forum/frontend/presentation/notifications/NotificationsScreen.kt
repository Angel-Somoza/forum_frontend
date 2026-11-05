package com.forum.frontend.presentation.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.Notification
import com.forum.frontend.domain.model.NotificationType
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.Toast
import com.forum.frontend.ui.components.cls
import com.forum.frontend.ui.format.timeAgo
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    unreadCount: Int,
    onOpen: (Notification) -> Unit,
    onMarkAllClick: () -> Unit,
    onDeleteClick: (Notification) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    Div({ classes("detail-header") }) {
        H2({ classes("section-title") }) { Text("Notificaciones") }
        Button({
            classes("link-button", "mark-all")
            if (unreadCount == 0 || state.isMarkingAll) disabled()
            onClick { onMarkAllClick() }
        }) {
            Icon("done_all")
            Text("Marcar leídas")
        }
    }

    when {
        state.isLoading -> LoadingCard()
        state.error != null -> ErrorCard(state.error, onRetry)
        state.isEmpty -> EmptyCard("notifications_off", "Todo tranquilo", "No tienes notificaciones")
        else -> {
            Div({ classes("card", "notification-list") }) {
                state.notifications.forEach { notification ->
                    key(notification.id) {
                        NotificationItem(
                            notification = notification,
                            onOpen = { onOpen(notification) },
                            onDelete = { onDeleteClick(notification) },
                        )
                    }
                }
            }
            if (state.hasMore) {
                Button({
                    classes("load-more")
                    if (state.isLoadingMore) disabled()
                    onClick { onLoadMore() }
                }) { Text(if (state.isLoadingMore) "Cargando..." else "Ver más") }
            }
        }
    }

    state.message?.let { Toast(it, onDismissMessage) }
}

@Composable
private fun NotificationItem(notification: Notification, onOpen: () -> Unit, onDelete: () -> Unit) {
    Div({
        classes(*cls("notification", if (!notification.isRead) "unread" else null))
        onClick { onOpen() }
    }) {
        Div({ classes("notification-avatar") }) {
            val badgeIcon = notification.badgeIconUrl
            if (notification.type == NotificationType.BADGE && badgeIcon != null) {
                Img(src = badgeIcon, alt = notification.badgeName.orEmpty(), attrs = { classes("badge-icon") })
            } else {
                Avatar(notification.actorFirstName, notification.actorLastName, notification.actorPictureUrl)
            }
            Span({ classes("type-icon") }) { Icon(notification.type.icon) }
        }
        Div({ classes("notification-body") }) {
            P({ classes("notification-text") }) {
                if (notification.type != NotificationType.BADGE && notification.actorName.isNotBlank()) {
                    Span({ classes("strong") }) { Text(notification.actorName) }
                    Text(" ")
                }
                Text(describe(notification))
            }
            notification.postPreview?.let { P({ classes("notification-preview") }) { Text(it) } }
            Span({ classes("author-meta") }) { Text(timeAgo(notification.createdAt)) }
        }
        if (!notification.isRead) Span({ classes("unread-dot") })
        Button({
            classes("link-button")
            title("Quitar")
            onClick {
                it.stopPropagation()
                onDelete()
            }
        }) { Icon("close") }
    }
}

private fun describe(notification: Notification): String = when (notification.type) {
    NotificationType.REACTION -> "le gustó tu publicación"
    NotificationType.COMMENT -> "comentó tu publicación"
    NotificationType.REPLY -> "respondió tu comentario"
    NotificationType.MENTION -> "te mencionó"
    NotificationType.SHARE -> "compartió tu publicación"
    NotificationType.BADGE -> "Ganaste la insignia ${notification.badgeName.orEmpty()}".trim()
    NotificationType.OTHER -> "tienes algo nuevo"
}
