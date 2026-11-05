package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.postdetail.PostDetailScreen
import com.forum.frontend.presentation.postdetail.PostDetailViewModel
import com.forum.frontend.ui.components.EmptyCard
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun PostPage() {
    val ctx = rememberPageContext()
    val postId = ctx.route.params["id"]?.toIntOrNull()
    val viewModel = rememberViewModel<PostDetailViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(postId) {
        document.title = "Publicación · Forum"
        if (postId != null) viewModel.load(postId)
    }

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) ctx.router.navigateTo("/")
    }

    if (postId == null) {
        EmptyCard("link_off", "Publicación no encontrada", "El enlace no es válido")
        return
    }

    PostDetailScreen(
        state = state,
        onBackClick = { ctx.router.navigateTo("/") },
        onPostLikeClick = viewModel::togglePostLike,
        onDeletePostClick = {
            if (window.confirm("¿Eliminar esta publicación?")) viewModel.deletePost()
        },
        onCommentLikeClick = viewModel::toggleCommentLike,
        onReplyClick = { id ->
            viewModel.replyTo(id)
            (document.getElementById("comment-input") as? HTMLElement)?.focus()
        },
        onDeleteCommentClick = { id ->
            if (window.confirm("¿Eliminar este comentario?")) viewModel.deleteComment(id)
        },
        onDraftChange = viewModel::onDraftChange,
        onCancelReply = viewModel::cancelReply,
        onSendClick = viewModel::sendComment,
        onRetry = viewModel::retry,
        onDismissMessage = viewModel::dismissMessage,
    )
}