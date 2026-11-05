package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.forum.frontend.ui.components.Icon
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun HomePage() {
    LaunchedEffect(Unit) { document.title = "Inicio · Forum" }
    Div({ classes("card", "empty-state") }) {
        Icon("dynamic_feed")
        H2 { Text("Publicaciones") }
        P { Text("Aquí aparecerán las publicaciones de tu espacio") }
    }
}