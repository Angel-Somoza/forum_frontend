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
fun BadgesPage() {
    LaunchedEffect(Unit) { document.title = "Insignias · Forum" }
    Div({ classes("card", "empty-state") }) {
        Icon("workspace_premium")
        H2 { Text("Insignias") }
        P { Text("Próximamente") }
    }
}