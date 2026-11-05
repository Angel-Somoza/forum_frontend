package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoadingCard() {
    Div({ classes("card", "loading-card") }) { Div({ classes("spinner") }) }
}

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Div({ classes("card", "empty-state", "error-card") }) {
        Icon("cloud_off")
        H2 { Text("No se pudo cargar") }
        P { Text(message) }
        Button({
            classes("btn-gold")
            onClick { onRetry() }
        }) { Text("Reintentar") }
    }
}

@Composable
fun EmptyCard(icon: String, title: String, message: String) {
    Div({ classes("card", "empty-state") }) {
        Icon(icon)
        H2 { Text(title) }
        P { Text(message) }
    }
}