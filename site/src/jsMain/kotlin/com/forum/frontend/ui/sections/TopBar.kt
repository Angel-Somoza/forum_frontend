package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.Icon
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun TopBar(
    user: CurrentUser,
    unreadCount: Int,
    onMenuClick: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    Header({ classes("topbar") }) {
        Div({ classes("topbar-row") }) {
            Button({
                classes("icon-button", "menu-button")
                onClick { onMenuClick() }
            }) { Icon("menu") }
            Div({ classes("topbar-spacer") })
            Button({
                classes("icon-button")
                onClick { onNavigate("/notifications") }
            }) {
                Icon("notifications")
                if (unreadCount > 0) {
                    Span({ classes("dot") }) { Text(if (unreadCount > 99) "99+" else unreadCount.toString()) }
                }
            }
            Button({
                classes("avatar-button")
                onClick { onNavigate("/profile") }
            }) {
                Avatar(user.firstName, user.lastName, user.profilePictureUrl)
            }
        }
        Div({ classes("topbar-greeting") }) {
            H1 { Text("Hola, ${user.firstName}") }
            P { Text("Descubre lo nuevo en tu espacio") }
        }
    }
}