package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun LoadingCard() {
    Div({
        classes("card", "loading-card")
        attr("role", "status")
    }) {
        BrandLogo("logo-loading")
        Span({ classes("loading-label") }) { Text("Cargando") }
    }
    Div({ classes("card", "skeleton-card") }) {
        Div({ classes("skeleton-row") }) {
            Div({ classes("skeleton", "skeleton-avatar") })
            Div({ classes("skeleton-lines") }) {
                Div({ classes("skeleton", "skeleton-line", "w-40") })
                Div({ classes("skeleton", "skeleton-line", "w-25") })
            }
        }
        Div({ classes("skeleton", "skeleton-line", "w-90") })
        Div({ classes("skeleton", "skeleton-line", "w-70") })
    }
}

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Div({ classes("card", "empty-state", "error-card") }) {
        Icon("cloud_off")
        H2 { Text("Ups") }
        P { Text(message) }
        Button({
            classes("btn-gold")
            onClick { onRetry() }
        }) { Text("Reintentar") }
    }
}

@Composable
fun EmptyCard(icon: String, title: String, message: String? = null) {
    Div({ classes("card", "empty-state") }) {
        Icon(icon)
        H2 { Text(title) }
        message?.let { P { Text(it) } }
    }
}
