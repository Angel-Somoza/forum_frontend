package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.cls
import org.jetbrains.compose.web.dom.Aside
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private data class SideItem(val path: String, val icon: String, val label: String)

private val items = listOf(
    SideItem("/", "home", "Inicio"),
    SideItem("/search", "explore", "Explorar"),
    SideItem("/notifications", "notifications", "Notificaciones"),
    SideItem("/profile", "person", "Perfil"),
)

@Composable
fun SideNav(
    currentPath: String,
    unreadCount: Int,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
) {
    Aside({ classes("side-nav") }) {
        Nav({ classes("side-menu") }) {
            items.forEach { item ->
                val active = item.path == currentPath
                Button({
                    classes(*cls("side-item", if (active) "active" else null))
                    if (active) attr("aria-current", "page")
                    onClick { onNavigate(item.path) }
                }) {
                    Span({ classes("side-icon") }) {
                        Icon(item.icon)
                        if (item.path == "/notifications" && unreadCount > 0) {
                            Span({ classes("nav-badge") }) { Text(if (unreadCount > 99) "99+" else unreadCount.toString()) }
                        }
                    }
                    Span { Text(item.label) }
                }
            }
        }
        Button({
            classes("side-publish")
            onClick { onNavigate("/new") }
        }) {
            Icon("edit_square")
            Span { Text("Publicar") }
        }
        Button({
            classes("side-logout")
            onClick { onLogoutClick() }
        }) {
            Icon("logout")
            Span { Text("Cerrar sesión") }
        }
    }
}
