package com.forum.frontend.presentation.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.SharedPostBox
import com.forum.frontend.ui.components.Toast
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.koin.mp.KoinPlatform
import org.w3c.dom.HTMLElement

@Composable
fun rememberShareController(): ShareController = remember { KoinPlatform.getKoin().get() }

@Composable
fun ShareDialog() {
    val controller = rememberShareController()
    val state by controller.state.collectAsState()

    state.notice?.let { Toast(it, controller::dismissNotice) }

    val target = state.target ?: return

    LaunchedEffect(target.postId) {
        (document.getElementById("share-comment") as? HTMLElement)?.focus()
    }

    Div({
        classes("modal-backdrop")
        onClick { controller.close() }
    }) {
        Div({
            classes("card", "modal", "share-modal")
            attr("role", "dialog")
            attr("aria-modal", "true")
            attr("aria-labelledby", "share-title")
            onClick { it.stopPropagation() }
            onKeyDown { if (it.key == "Escape") controller.close() }
        }) {
            Div({ classes("modal-head") }) {
                H2({
                    id("share-title")
                    classes("section-title")
                }) { Text("Compartir publicación") }
                Button({
                    classes("round-button", "small")
                    title("Cerrar")
                    onClick { controller.close() }
                }) { Icon("close") }
            }
            TextArea(value = state.comment) {
                id("share-comment")
                classes("composer-input", "share-input")
                placeholder("Agrega un comentario (opcional)")
                attr("maxlength", ShareUiState.MAX_LENGTH.toString())
                onInput { controller.onCommentChange(it.value) }
            }
            SharedPostBox(target.preview, onOpenPost = null)
            state.error?.let { P({ classes("form-error") }) { Text(it) } }
            Div({ classes("composer-actions", "modal-actions") }) {
                Button({
                    classes("btn-ghost")
                    onClick { controller.close() }
                }) { Text("Cancelar") }
                Button({
                    classes("btn-gold")
                    if (state.isSubmitting) disabled()
                    onClick { controller.submit() }
                }) { Text(if (state.isSubmitting) "Compartiendo..." else "Compartir") }
            }
        }
    }
}
