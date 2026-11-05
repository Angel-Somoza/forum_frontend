package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.cls
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun BottomNav(
    currentPath: String,
    unreadCount: Int,
    onNavigate: (String) -> Unit,
    onCreateClick: () -> Unit,
) {
    Nav({ classes("bottom-nav") }) {
        NavButton("/", "home", "Inicio", currentPath, 0, onNavigate)
        NavButton("/search", "search", "Buscar", currentPath, 0, onNavigate)
        Button({
            classes("nav-create")
            title("Nueva publicación")
            onClick { onCreateClick() }
        }) { Icon("add") }
        NavButton("/notifications", "notifications", "Notificaciones", currentPath, unreadCount, onNavigate)
        NavButton("/profile", "person", "Perfil", currentPath, 0, onNavigate)
    }
}

@Composable
private fun NavButton(
    path: String,
    icon: String,
    label: String,
    currentPath: String,
    badge: Int,
    onNavigate: (String) -> Unit,
) {
    Button({
        classes(*cls("nav-button", if (path == currentPath) "active" else null))
        title(label)
        onClick { onNavigate(path) }
    }) {
        Icon(icon)
        if (badge > 0) {
            Span({ classes("nav-badge") }) { Text(if (badge > 99) "99+" else badge.toString()) }
        }
    }
}