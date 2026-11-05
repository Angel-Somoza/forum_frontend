package com.forum.frontend.presentation.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.ui.components.ChipGroup
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.PostCard
import com.forum.frontend.ui.components.Toast
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.Button
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
) {
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