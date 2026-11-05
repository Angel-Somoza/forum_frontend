package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span

@Composable
fun BrandLogo(vararg modifiers: String) {
    Div({
        classes("logo", *modifiers)
        attr("aria-hidden", "true")
    }) {
        Span({ classes("bubble", "bubble-a") }) {
            Span({ classes("bubble-dot") })
            Span({ classes("bubble-dot") })
            Span({ classes("bubble-dot") })
        }
        Span({ classes("bubble", "bubble-b") })
    }
}
