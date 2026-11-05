package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Toast(message: String, onDismiss: () -> Unit) {
    LaunchedEffect(message) {
        delay(3500)
        onDismiss()
    }
    Div({
        classes("toast")
        attr("role", "status")
        onClick { onDismiss() }
    }) { Text(message) }
}
