package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.Media
import com.forum.frontend.domain.model.MediaType
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.SharedPost
import com.forum.frontend.ui.format.compactCount
import com.forum.frontend.ui.format.timeAgo
import org.jetbrains.compose.web.dom.Article
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Video

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    expanded: Boolean = false,
    onDeleteClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onOpenPost: ((Int) -> Unit)? = null,
) {
    val cover = post.coverMedia
    if (cover != null && !post.isShare) {
        MediaPostCard(post, cover, expanded, onLikeClick, onCommentsClick, onDeleteClick, onShareClick)
    } else {
        TextPostCard(post, onLikeClick, onCommentsClick, onDeleteClick, onShareClick, onOpenPost)
    }
}

@Composable
private fun MediaPostCard(
    post: Post,
    cover: Media,
    expanded: Boolean,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onShareClick: (() -> Unit)?,
) {
    Article({ classes("post-card", "post-card-media") }) {
        Img(src = cover.url, alt = "", attrs = {
            classes("post-cover")
            attr("loading", "lazy")
        })
        if (post.media.size > 1) {
            Span({ classes("media-count") }) {
                Icon("photo_library")
                Text(post.media.size.toString())
            }
        }
        Div({ classes("post-overlay-top") }) {
            Div({ classes("author-pill") }) {
                Avatar(post.author.firstName, post.author.lastName, post.author.pictureUrl, AvatarSize.SMALL)
                Div({ classes("author-pill-text") }) {
                    Span({ classes("author-name") }) { Text(post.author.fullName) }
                    Span({ classes("author-meta") }) { Text(timeAgo(post.createdAt)) }
                }
            }
            Div({ classes("overlay-actions") }) {
                Span({ classes("space-tag") }) { Text(post.spaceName) }
                onDeleteClick?.let { DeleteButton(it) }
            }
        }
        Div({ classes("post-overlay-bottom") }) {
            if (post.content.isNotBlank()) PostText(post.content, clamp = !expanded)
            PostStats(post, onLikeClick, onCommentsClick, onShareClick)
        }
    }
}

@Composable
private fun TextPostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onDeleteClick: (() -> Unit)?,
    onShareClick: (() -> Unit)?,
    onOpenPost: ((Int) -> Unit)?,
) {
    Article({ classes("card", "post-card") }) {
        if (post.isShare) {
            Div({ classes("share-label") }) {
                Icon("repeat")
                Text("${post.author.firstName} compartió")
            }
        }
        Div({ classes("post-header") }) {
            Avatar(post.author.firstName, post.author.lastName, post.author.pictureUrl)
            Div({ classes("post-header-text") }) {
                Span({ classes("author-name") }) { Text(post.author.fullName) }
                Span({ classes("author-meta") }) { Text("${post.author.role.label} · ${timeAgo(post.createdAt)}") }
            }
            Span({ classes("space-tag") }) { Text(post.spaceName) }
            onDeleteClick?.let { DeleteButton(it) }
        }
        if (post.content.isNotBlank()) PostText(post.content)
        MediaGallery(post.media)
        if (post.isShare) SharedPostBox(post.sharedPost, onOpenPost)
        PostStats(post, onLikeClick, onCommentsClick, onShareClick)
    }
}

@Composable
fun MediaGallery(media: List<Media>, compact: Boolean = false) {
    if (media.isEmpty()) return
    val visible = media.take(MAX_GALLERY)
    val extra = media.size - visible.size
    Div({
        classes(*cls("media-gallery", "count-${visible.size}", if (compact) "compact" else null))
        onClick { it.stopPropagation() }
    }) {
        visible.forEachIndexed { index, item ->
            Div({ classes("gallery-item") }) {
                if (item.type == MediaType.VIDEO) {
                    Video({
                        classes("gallery-media")
                        attr("src", item.url)
                        attr("controls", "")
                        attr("preload", "metadata")
                        attr("playsinline", "")
                    })
                } else {
                    Img(src = item.url, alt = "", attrs = {
                        classes("gallery-media")
                        attr("loading", "lazy")
                    })
                    if (item.type == MediaType.GIF) Span({ classes("gif-tag") }) { Text("GIF") }
                }
                if (extra > 0 && index == visible.lastIndex) {
                    Span({ classes("gallery-more") }) { Text("+$extra") }
                }
            }
        }
    }
}

@Composable
fun SharedPostBox(shared: SharedPost?, onOpenPost: ((Int) -> Unit)?) {
    if (shared == null) {
        Div({ classes("shared-post", "unavailable") }) {
            Icon("visibility_off")
            Span { Text("Esta publicación ya no está disponible") }
        }
        return
    }
    Div({
        classes(*cls("shared-post", if (onOpenPost != null) "clickable" else null))
        onOpenPost?.let { open -> onClick { open(shared.id) } }
    }) {
        Div({ classes("shared-head") }) {
            Avatar(shared.author.firstName, shared.author.lastName, shared.author.pictureUrl, AvatarSize.SMALL)
            Div({ classes("post-header-text") }) {
                Span({ classes("author-name") }) { Text(shared.author.fullName) }
                Span({ classes("author-meta") }) {
                    Text("${shared.author.role.label} · ${timeAgo(shared.createdAt)}")
                }
            }
        }
        if (shared.content.isNotBlank()) PostText(shared.content, clamp = true)
        MediaGallery(shared.media, compact = true)
    }
}

@Composable
private fun DeleteButton(onClick: () -> Unit) {
    Button({
        classes("round-button", "small")
        title("Eliminar publicación")
        onClick { onClick() }
    }) { Icon("delete") }
}

@Composable
private fun PostStats(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentsClick: () -> Unit,
    onShareClick: (() -> Unit)?,
) {
    Div({ classes("post-stats") }) {
        Button({
            classes(*cls("stat-button", if (post.isLiked) "liked" else null))
            title(if (post.isLiked) "Quitar me gusta" else "Me gusta")
            onClick { onLikeClick() }
        }) {
            Icon("favorite")
            Span { Text(compactCount(post.reactionCount)) }
        }
        Button({
            classes("stat-button")
            title("Comentarios")
            onClick { onCommentsClick() }
        }) {
            Icon("chat_bubble")
            Span { Text(compactCount(post.commentCount)) }
        }
        if (onShareClick != null) {
            Button({
                classes("stat-button", "share")
                title("Compartir")
                onClick { onShareClick() }
            }) {
                Icon("repeat")
                Span { Text(compactCount(post.shareCount)) }
            }
        }
    }
}

private const val MAX_GALLERY = 4
