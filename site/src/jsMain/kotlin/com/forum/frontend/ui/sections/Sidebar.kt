package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.cls
import org.jetbrains.compose.web.dom.Aside
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private data class NavEntry(val path: String, val icon: String, val label: String)

private val navEntries = listOf(
    NavEntry("/", "home", "Inicio"),
    NavEntry("/profile", "person", "Perfil"),
    NavEntry("/notifications", "notifications", "Notificaciones"),
)

@Composable
fun Sidebar(
    user: CurrentUser,
    currentPath: String,
    isOpen: Boolean,
    unreadCount: Int,
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    Div({
        classes(*cls("sidebar-backdrop", if (isOpen) "open" else null))
        onClick { onClose() }
    })
    Aside({ classes(*cls("sidebar", if (isOpen) "open" else null)) }) {
        Button({
            classes("icon-button", "sidebar-close")
            onClick { onClose() }
        }) { Icon("close") }
        Div({ classes("sidebar-user") }) {
            Avatar(user.firstName, user.lastName, user.profilePictureUrl)
            Div {
                Div({ classes("sidebar-user-name") }) { Text(user.fullName) }
                Div({ classes("sidebar-user-role") }) { Text(user.role.label) }
            }
        }
        Nav({ classes("sidebar-nav") }) {
            navEntries.forEach { entry ->
                Button({
                    classes(*cls("nav-item", if (entry.path == currentPath) "active" else null))
                    onClick { onNavigate(entry.path) }
                }) {
                    Icon(entry.icon)
                    Span { Text(entry.label) }
                    if (entry.path == "/notifications" && unreadCount > 0) {
                        Span({ classes("nav-badge") }) { Text(if (unreadCount > 99) "99+" else unreadCount.toString()) }
                    }
                }
            }
        }
        Button({
            classes("nav-item")
            onClick { onLogout() }
        }) {
            Icon("logout")
            Span { Text("Cerrar sesión") }
        }
    }
}