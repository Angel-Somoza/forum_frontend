package com.forum.frontend.presentation.postdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.CommentNode
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.AvatarSize
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.PostCard
import com.forum.frontend.ui.components.PostText
import com.forum.frontend.ui.components.cls
import com.forum.frontend.ui.format.compactCount
import com.forum.frontend.ui.format.timeAgo
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun PostDetailScreen(
    state: PostDetailUiState,
    onBackClick: () -> Unit,
    onPostLikeClick: () -> Unit,
    onDeletePostClick: () -> Unit,
    onCommentLikeClick: (Int) -> Unit,
    onReplyClick: (Int) -> Unit,
    onDeleteCommentClick: (Int) -> Unit,
    onDraftChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSendClick: () -> Unit,
    onRetry: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    Div({ classes("detail-header") }) {
        Button({
            classes("round-button")
            title("Volver")
            onClick { onBackClick() }
        }) { Icon("arrow_back") }
        H2({ classes("section-title") }) { Text("Publicación") }
    }

    when {
        state.isLoading -> LoadingCard()
        state.error != null -> ErrorCard(state.error, onRetry)
        state.post != null -> {
            val post = state.post
            PostCard(
                post = post,
                onLikeClick = onPostLikeClick,
                onCommentsClick = {},
                expanded = true,
                onDeleteClick = if (state.canDelete(post.author.id)) onDeletePostClick else null,
            )
            H2({ classes("section-title") }) { Text("Comentarios (${post.commentCount})") }
            Div({ classes("card", "comments-card") }) {
                if (state.thread.isEmpty()) {
                    P({ classes("muted") }) { Text("Aún no hay comentarios. Sé el primero en opinar.") }
                }
                state.thread.forEach { node ->
                    key(node.comment.id) {
                        CommentItem(node, state, onCommentLikeClick, onReplyClick, onDeleteCommentClick)
                    }
                }
            }
            CommentComposer(state, onDraftChange, onCancelReply, onSendClick)
        }
    }

    state.message?.let { message ->
        Div({
            classes("toast")
            onClick { onDismissMessage() }
        }) { Text(message) }
    }
}

@Composable
private fun CommentItem(
    node: CommentNode,
    state: PostDetailUiState,
    onLikeClick: (Int) -> Unit,
    onReplyClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    val comment = node.comment
    Div({ classes("comment") }) {
        Avatar(comment.author.firstName, comment.author.lastName, comment.author.pictureUrl, AvatarSize.SMALL)
        Div({ classes("comment-body") }) {
            Div({ classes("comment-bubble") }) {
                Div({ classes("comment-head") }) {
                    Span({ classes("author-name") }) { Text(comment.author.fullName) }
                    Span({ classes("author-meta") }) { Text(timeAgo(comment.createdAt)) }
                }
                PostText(comment.content)
            }
            Div({ classes("comment-actions") }) {
                Button({
                    classes(*cls("comment-action", if (comment.isLiked) "liked" else null))
                    onClick { onLikeClick(comment.id) }
                }) {
                    Icon("favorite")
                    if (comment.reactionCount > 0) Span { Text(compactCount(comment.reactionCount)) }
                }
                Button({
                    classes("comment-action")
                    onClick { onReplyClick(comment.id) }
                }) { Text("Responder") }
                if (state.canDelete(comment.author.id)) {
                    Button({
                        classes("comment-action")
                        onClick { onDeleteClick(comment.id) }
                    }) { Text("Eliminar") }
                }
            }
            if (node.replies.isNotEmpty()) {
                Div({ classes("comment-replies") }) {
                    node.replies.forEach { reply ->
                        key(reply.comment.id) {
                            CommentItem(reply, state, onLikeClick, onReplyClick, onDeleteClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentComposer(
    state: PostDetailUiState,
    onDraftChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSendClick: () -> Unit,
) {
    Form(attrs = {
        classes("comment-composer")
        addEventListener("submit") {
            it.preventDefault()
            onSendClick()
        }
    }) {
        state.replyingTo?.let { target ->
            Div({ classes("replying-to") }) {
                Span { Text("Respondiendo a ${target.author.fullName}") }
                Button({
                    classes("link-button")
                    type(ButtonType.Button)
                    onClick { onCancelReply() }
                }) { Icon("close") }
            }
        }
        Div({ classes("comment-input-row") }) {
            Input(InputType.Text) {
                id("comment-input")
                classes("comment-input")
                placeholder(if (state.replyingTo != null) "Escribe tu respuesta..." else "Escribe un comentario...")
                attr("maxlength", PostDetailUiState.MAX_COMMENT_LENGTH.toString())
                value(state.draft)
                onInput { onDraftChange(it.value) }
            }
            Button(attrs = {
                classes("send-button")
                type(ButtonType.Submit)
                if (!state.canSend) disabled()
            }) { Icon("send") }
        }
    }
}