package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.feed.FeedScreen
import com.forum.frontend.presentation.feed.FeedViewModel
import com.forum.frontend.presentation.share.rememberShareController
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document
import kotlinx.browser.window

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun HomePage() {
    val ctx = rememberPageContext()
    val viewModel = rememberViewModel<FeedViewModel>()
    val shareController = rememberShareController()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { document.title = "Inicio · Forum" }

    FeedScreen(
        state = state,
        onSpaceSelect = viewModel::selectSpace,
        onLikeClick = viewModel::toggleLike,
        onCommentsClick = { id -> ctx.router.navigateTo("/post?id=$id") },
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadMore,
        onDismissMessage = viewModel::dismissMessage,
        onComposeClick = { ctx.router.navigateTo("/new") },
        onShareClick = shareController::open,
        onOpenPost = { id -> ctx.router.navigateTo("/post?id=$id") },
        canDelete = viewModel::canDelete,
        onDeleteClick = { post ->
            if (window.confirm("¿Eliminar esta publicación?")) viewModel.deletePost(post.id)
        },
    )
}