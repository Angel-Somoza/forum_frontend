package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.Icon
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun AppHeader(
    user: CurrentUser,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Header({ classes("app-header") }) {
        Button({
            classes("header-user")
            onClick { onProfileClick() }
        }) {
            Avatar(user.firstName, user.lastName, user.profilePictureUrl)
            Div({ classes("header-user-text") }) {
                Span({ classes("header-name") }) { Text(user.fullName) }
                Span({ classes("header-handle") }) { Text(user.role.label) }
            }
        }
        Div({ classes("header-actions") }) {
            Button({
                classes("round-button")
                title("Cerrar sesión")
                onClick { onLogoutClick() }
            }) { Icon("logout") }
        }
    }
}