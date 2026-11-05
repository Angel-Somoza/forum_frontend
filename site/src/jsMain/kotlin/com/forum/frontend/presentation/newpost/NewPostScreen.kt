package com.forum.frontend.presentation.newpost

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.MediaType
import com.forum.frontend.ui.components.ChipGroup
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.cls
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.dom.Video
import org.w3c.files.File

private data class Picker(val id: String, val icon: String, val label: String, val accept: String)

private val pickers = listOf(
    Picker("pick-photo", "image", "Foto", "image/jpeg,image/png,image/webp"),
    Picker("pick-video", "videocam", "Video", "video/mp4,video/webm"),
    Picker("pick-gif", "gif_box", "GIF", "image/gif"),
)

@Composable
fun NewPostScreen(
    state: NewPostUiState,
    onContentChange: (String) -> Unit,
    onSpaceSelect: (Int) -> Unit,
    onFilesSelected: (List<File>) -> Unit,
    onRemoveAttachment: (Int) -> Unit,
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

        if (state.attachments.isNotEmpty()) {
            Div({ classes("attachment-grid") }) {
                state.attachments.forEach { attachment ->
                    key(attachment.key) { AttachmentTile(attachment, onRemove = { onRemoveAttachment(attachment.key) }) }
                }
            }
        }

        Div({ classes("media-pickers") }) {
            pickers.forEach { picker ->
                Input(InputType.File) {
                    id(picker.id)
                    classes("visually-hidden")
                    attr("accept", picker.accept)
                    attr("multiple", "")
                    if (!state.canAttach) disabled()
                    onChange { event ->
                        val input = event.target
                        val list = input.files
                        val files = if (list == null) emptyList() else (0 until list.length).mapNotNull { list.item(it) }
                        input.value = ""
                        if (files.isNotEmpty()) onFilesSelected(files)
                    }
                }
                Label(forId = picker.id, attrs = {
                    classes(*cls("media-picker", if (!state.canAttach) "disabled" else null))
                    title("Agregar ${picker.label.lowercase()}")
                }) {
                    Icon(picker.icon)
                    Span { Text(picker.label) }
                }
            }
        }

        state.error?.let { message ->
            P({ classes("form-error") }) { Text(message) }
        }

        Div({ classes("composer-footer") }) {
            Span({ classes(*cls("char-count", if (state.remaining < 200) "warn" else null)) }) {
                when {
                    state.isUploading -> Text("Subiendo archivos...")
                    state.remaining < 200 -> Text(state.remaining.toString())
                }
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

@Composable
private fun AttachmentTile(attachment: Attachment, onRemove: () -> Unit) {
    Div({ classes(*cls("attachment", if (attachment.isUploading) "uploading" else null)) }) {
        if (attachment.type == MediaType.VIDEO) {
            Video({
                classes("attachment-media")
                attr("src", attachment.previewUrl)
                attr("muted", "")
                attr("playsinline", "")
                attr("preload", "metadata")
            })
            Span({ classes("attachment-kind") }) { Icon("play_arrow") }
        } else {
            Img(src = attachment.previewUrl, alt = "", attrs = { classes("attachment-media") })
            if (attachment.type == MediaType.GIF) Span({ classes("attachment-kind", "text") }) { Text("GIF") }
        }
        if (attachment.isUploading) Div({ classes("attachment-progress") }) { Div({ classes("spinner") }) }
        Button({
            classes("attachment-remove")
            title("Quitar")
            onClick { onRemove() }
        }) { Icon("close") }
    }
}
