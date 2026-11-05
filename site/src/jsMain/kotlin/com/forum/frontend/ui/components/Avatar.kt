package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

enum class AvatarSize(val css: String) { SMALL("avatar-sm"), MEDIUM("avatar-md"), LARGE("avatar-lg") }

@Composable
fun Avatar(firstName: String, lastName: String, url: String?, size: AvatarSize = AvatarSize.MEDIUM) {
    if (!url.isNullOrBlank()) {
        Img(src = url, alt = "$firstName $lastName", attrs = { classes("avatar", size.css) })
    } else {
        val initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
        Div({ classes("avatar", size.css, "avatar-initials") }) { Text(initials) }
    }
}