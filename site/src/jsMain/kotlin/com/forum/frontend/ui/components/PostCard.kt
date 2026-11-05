package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import com.forum.frontend.domain.model.Media
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
) {
    val cover = post.coverMedia
    if (cover != null) {
        MediaPostCard(post, cover, onLikeClick, onCommentsClick)
    } else {
        TextPostCard(post, onLikeClick, onCommentsClick)
    }
}

@Composable
private fun MediaPostCard(post: Post, cover: Media, onLikeClick: () -> Unit, onCommentsClick: () -> Unit) {
    Article({ classes("post-card", "post-card-media") }) {
        Img(src = cover.url, alt = "", attrs = {
            classes("post-cover")
            attr("loading", "lazy")
        })
        Div({ classes("post-overlay-top") }) {
            Div({ classes("author-pill") }) {
                Avatar(post.author.firstName, post.author.lastName, post.author.pictureUrl, AvatarSize.SMALL)
                Div({ classes("author-pill-text") }) {
                    Span({ classes("author-name") }) { Text(post.author.fullName) }
                    Span({ classes("author-meta") }) { Text(timeAgo(post.createdAt)) }
                }
            }
            Span({ classes("space-tag") }) { Text(post.spaceName) }
        }
        Div({ classes("post-overlay-bottom") }) {
            if (post.content.isNotBlank()) PostText(post.content, clamp = true)
            PostStats(post, onLikeClick, onCommentsClick)
        }
    }
}

@Composable
private fun TextPostCard(post: Post, onLikeClick: () -> Unit, onCommentsClick: () -> Unit) {
    Article({ classes("card", "post-card") }) {
        Div({ classes("post-header") }) {
            Avatar(post.author.firstName, post.author.lastName, post.author.pictureUrl)
            Div({ classes("post-header-text") }) {
                Span({ classes("author-name") }) { Text(post.author.fullName) }
                Span({ classes("author-meta") }) { Text("${post.author.role.label} · ${timeAgo(post.createdAt)}") }
            }
            Span({ classes("space-tag") }) { Text(post.spaceName) }
        }
        if (post.content.isNotBlank()) PostText(post.content)
        post.sharedPost?.let { SharedPostBox(it) }
        post.videos.forEach { video ->
            Video({
                classes("post-video")
                attr("src", video.url)
                attr("controls", "")
                attr("preload", "metadata")
            })
        }
        PostStats(post, onLikeClick, onCommentsClick)
    }
}

@Composable
private fun SharedPostBox(shared: SharedPost) {
    Div({ classes("shared-post") }) {
        Div({ classes("shared-post-author") }) { Text("${shared.authorName} · ${timeAgo(shared.createdAt)}") }
        PostText(shared.content, clamp = true)
    }
}

@Composable
private fun PostStats(post: Post, onLikeClick: () -> Unit, onCommentsClick: () -> Unit) {
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
    }
}