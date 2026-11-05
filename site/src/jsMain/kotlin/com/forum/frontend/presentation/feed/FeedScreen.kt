package com.forum.frontend.presentation.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.Post
import com.forum.frontend.ui.components.ChipGroup
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.PostCard
import com.forum.frontend.ui.components.Toast
import org.jetbrains.compose.web.attributes.disabled
import com.forum.frontend.ui.components.Icon
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

@Composable
fun FeedScreen(
    state: FeedUiState,
    onSpaceSelect: (Int?) -> Unit,
    onLikeClick: (Int) -> Unit,
    onCommentsClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onDismissMessage: () -> Unit,
    onComposeClick: () -> Unit = {},
    onShareClick: (Post) -> Unit = {},
    onOpenPost: (Int) -> Unit = {},
) {
    Div({ classes("card", "feed-composer") }) {
        Button({
            classes("feed-composer-input")
            onClick { onComposeClick() }
        }) { Text("¿Qué quieres compartir hoy?") }
        Div({ classes("feed-composer-actions") }) {
            listOf("image" to "Foto", "videocam" to "Video", "gif_box" to "GIF").forEach { (icon, label) ->
                Button({
                    classes("composer-chip")
                    title(label)
                    onClick { onComposeClick() }
                }) {
                    Icon(icon)
                    Span { Text(label) }
                }
            }
            Button({
                classes("btn-gold")
                onClick { onComposeClick() }
            }) { Text("Publicar") }
        }
    }

    if (state.showSpaceFilter) {
        ChipGroup(
            options = listOf<Pair<Int?, String>>(null to "Todos") + state.spaces.map { it.id to it.name },
            selected = state.selectedSpaceId,
            onSelect = onSpaceSelect,
        )
    }

    H2({ classes("section-title") }) { Text("Lo último") }

    when {
        state.isLoading -> LoadingCard()
        state.error != null -> ErrorCard(state.error, onRetry)
        state.isEmpty -> EmptyCard(icon = "dynamic_feed", title = "Nada por aquí todavía")
        else -> {
            state.posts.forEach { post ->
                key(post.id) {
                    PostCard(
                        post = post,
                        onLikeClick = { onLikeClick(post.id) },
                        onCommentsClick = { onCommentsClick(post.id) },
                        onShareClick = { onShareClick(post) },
                        onOpenPost = onOpenPost,
                    )
                }
            }
            if (state.hasMore) {
                Button({
                    classes("load-more")
                    if (state.isLoadingMore) disabled()
                    onClick { onLoadMore() }
                }) {
                    Text(if (state.isLoadingMore) "Cargando..." else "Ver más")
                }
            }
        }
    }

    state.message?.let { Toast(it, onDismissMessage) }
}