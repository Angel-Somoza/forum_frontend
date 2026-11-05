package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private val hashtagRegex = Regex("#[A-Za-z0-9_áéíóúÁÉÍÓÚñÑüÜ]+")

@Composable
fun PostText(text: String, clamp: Boolean = false) {
    P({ classes(*cls("post-text", if (clamp) "clamp" else null)) }) {
        var cursor = 0
        hashtagRegex.findAll(text).forEach { match ->
            if (match.range.first > cursor) Text(text.substring(cursor, match.range.first))
            Span({ classes("hashtag") }) { Text(match.value) }
            cursor = match.range.last + 1
        }
        if (cursor < text.length) Text(text.substring(cursor))
    }
}