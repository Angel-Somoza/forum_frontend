package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun Icon(name: String) {
    Span({ classes("material-symbols-rounded") }) { Text(name) }
}