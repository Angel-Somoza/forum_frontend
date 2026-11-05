package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.AvatarSize
import com.forum.frontend.ui.components.Icon
import org.jetbrains.compose.web.dom.Aside
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun RightRail(
    user: CurrentUser,
    currentPath: String,
    onNavigate: (String) -> Unit,
) {
    Aside({ classes("right-rail") }) {
        if (currentPath != "/profile") Div({ classes("card", "rail-profile") }) {
            Div({ classes("rail-cover") })
            Div({ classes("rail-avatar") }) {
                Avatar(user.firstName, user.lastName, user.profilePictureUrl, AvatarSize.LARGE)
            }
            Span({ classes("rail-name") }) { Text(user.fullName) }
            Span({ classes("role-chip") }) { Text(user.role.label) }
            Button({
                classes("btn-gold", "rail-publish")
                onClick { onNavigate("/new") }
            }) { Text("Publicar") }
        }
        if (user.spaces.isNotEmpty()) {
            Div({ classes("card", "rail-list") }) {
                Span({ classes("rail-title") }) { Text("Tus espacios") }
                user.spaces.forEach { space ->
                    Div({ classes("rail-row") }) {
                        Icon("forum")
                        Span { Text(space.name) }
                    }
                }
            }
        }
        Div({ classes("card", "rail-list") }) {
            Span({ classes("rail-title") }) { Text("Comparte algo") }
            RailAction("image", "Foto", onClick = { onNavigate("/new") })
            RailAction("videocam", "Video", onClick = { onNavigate("/new") })
            RailAction("gif_box", "GIF", onClick = { onNavigate("/new") })
        }
    }
}

@Composable
private fun RailAction(icon: String, label: String, onClick: () -> Unit) {
    Button({
        classes("rail-row", "rail-action")
        onClick { onClick() }
    }) {
        Icon(icon)
        Span { Text(label) }
        Icon("chevron_right")
    }
}
