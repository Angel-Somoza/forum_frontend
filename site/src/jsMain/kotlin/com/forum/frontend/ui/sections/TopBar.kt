package com.forum.frontend.ui.sections

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.AvatarSize
import com.forum.frontend.ui.components.BrandLogo
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.QuickSearch
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun TopBar(
    user: CurrentUser,
    onNavigate: (String) -> Unit,
) {
    Header({ classes("top-bar") }) {
        Button({
            classes("top-brand")
            title("Inicio")
            onClick { onNavigate("/") }
        }) {
            BrandLogo()
            Span { Text("Forum") }
        }
        QuickSearch(onNavigate)
        Button({
            classes("top-user")
            onClick { onNavigate("/profile") }
        }) {
            Avatar(user.firstName, user.lastName, user.profilePictureUrl, AvatarSize.SMALL)
            Span({ classes("top-user-name") }) { Text(user.firstName) }
            Icon("expand_more")
        }
    }
}
