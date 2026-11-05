package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.forum.frontend.core.LoadingTracker
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.koin.mp.KoinPlatform

@Composable
fun GlobalLoader() {
    val tracker = remember { KoinPlatform.getKoin().get<LoadingTracker>() }
    val pending by tracker.pending.collectAsState()
    val blocking by tracker.blocking.collectAsState()
    val label = blocking.lastOrNull()

    Div({
        classes(*cls("top-progress", if (pending > 0) "active" else null))
        attr("aria-hidden", "true")
    })

    Div({
        classes(*cls("loading-screen", if (label != null) "visible" else null))
        attr("aria-hidden", if (label != null) "false" else "true")
        attr("aria-live", "polite")
    }) {
        BrandLogo("logo-lg", "logo-loading")
        Span({ classes("loading-label") }) { Text(label.orEmpty()) }
    }
}

@Composable
fun ScreenLoader() {
    Div({ classes("screen-loading") }) { BrandLogo("logo-lg", "logo-loading") }
}
