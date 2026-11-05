package com.forum.frontend.data.mapper

import com.forum.frontend.data.remote.dto.CommentDto
import com.forum.frontend.domain.model.Author
import com.forum.frontend.domain.model.Comment
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.model.Role

fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    postId = postId,
    author = Author(
        id = userId,
        firstName = authorFirstName,
        lastName = authorLastName,
        pictureUrl = authorPictureUrl?.takeIf { it.isNotBlank() },
        role = Role.UNKNOWN,
    ),
    parentId = parentId,
    content = content.orEmpty(),
    createdAt = parseApiDate(createdAt),
    reactionCount = reactionCount,
    myReaction = myReaction?.let { ReactionType.fromApi(it) },
)