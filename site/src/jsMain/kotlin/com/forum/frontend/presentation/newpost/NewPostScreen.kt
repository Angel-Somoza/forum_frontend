package com.forum.frontend.presentation.newpost

import androidx.compose.runtime.Composable
import com.forum.frontend.ui.components.ChipGroup
import com.forum.frontend.ui.components.cls
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea

@Composable
fun NewPostScreen(
    state: NewPostUiState,
    onContentChange: (String) -> Unit,
    onSpaceSelect: (Int) -> Unit,
    onPublishClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Div({ classes("card", "composer") }) {
        H2({ classes("section-title") }) { Text("Nueva publicación") }

        if (state.spaces.isEmpty()) {
            P({ classes("muted") }) {
                Text("Todavía no estás en ningún espacio")
            }
            return@Div
        }

        if (state.spaces.size > 1) {
            Span({ classes("composer-label") }) { Text("Publicar en") }
            ChipGroup(
                options = state.spaces.map { it.id to it.name },
                selected = state.selectedSpaceId,
                onSelect = { id -> if (id != null) onSpaceSelect(id) },
            )
        }

        TextArea(value = state.content) {
            classes("composer-input")
            placeholder("¿Qué estás pensando?")
            attr("maxlength", NewPostUiState.MAX_LENGTH.toString())
            onInput { onContentChange(it.value) }
        }

        state.error?.let { message ->
            P({ classes("form-error") }) { Text(message) }
        }

        Div({ classes("composer-footer") }) {
            Span({ classes(*cls("char-count", if (state.remaining < 200) "warn" else null)) }) {
                if (state.remaining < 200) Text(state.remaining.toString())
            }
            Div({ classes("composer-actions") }) {
                Button({
                    classes("btn-ghost")
                    onClick { onCancelClick() }
                }) { Text("Cancelar") }
                Button({
                    classes("btn-gold")
                    if (!state.canSubmit) disabled()
                    onClick { onPublishClick() }
                }) { Text(if (state.isSubmitting) "Publicando..." else "Publicar") }
            }
        }
    }
}