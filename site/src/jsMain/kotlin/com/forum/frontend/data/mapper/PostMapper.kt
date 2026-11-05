package com.forum.frontend.data.mapper

import com.forum.frontend.core.toSafeUrl
import com.forum.frontend.data.remote.dto.MediaDto
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.PostDto
import com.forum.frontend.data.remote.dto.SharedPostDto
import com.forum.frontend.domain.model.Author
import com.forum.frontend.domain.model.Media
import com.forum.frontend.domain.model.MediaType
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.model.Role
import com.forum.frontend.domain.model.SharedPost
import kotlin.js.Date

fun parseApiDate(value: String): Double = Date(value).getTime()

fun <T, R> PageResponse<T>.toPaged(transform: (T) -> R): Paged<R> =
    Paged(items = data.map(transform), page = meta.page, totalPages = meta.totalPages)

fun MediaDto.toDomain(): Media? {
    val safe = url.toSafeUrl() ?: return null
    return MediaType.fromApi(type)?.let { Media(id = id, type = it, url = safe) }
}

fun SharedPostDto.toDomain(): SharedPost = SharedPost(
    id = id,
    authorName = "$authorFirstName $authorLastName",
    authorPictureUrl = authorPictureUrl.toSafeUrl(),
    content = content.orEmpty(),
    createdAt = parseApiDate(createdAt),
)

fun PostDto.toDomain(): Post = Post(
    id = id,
    author = Author(
        id = userId,
        firstName = authorFirstName,
        lastName = authorLastName,
        pictureUrl = authorPictureUrl.toSafeUrl(),
        role = Role.fromApi(authorRoleName),
    ),
    spaceId = spaceId,
    spaceName = spaceName,
    content = content.orEmpty(),
    createdAt = parseApiDate(createdAt),
    media = media.sortedBy { it.sortOrder }.mapNotNull { it.toDomain() },
    reactionCount = reactionCount,
    myReaction = myReaction?.let { ReactionType.fromApi(it) },
    commentCount = commentCount,
    sharedPost = sharedPost?.toDomain(),
)