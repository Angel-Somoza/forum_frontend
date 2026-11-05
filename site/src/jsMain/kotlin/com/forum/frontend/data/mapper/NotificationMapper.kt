package com.forum.frontend.data.mapper

import com.forum.frontend.core.toSafeUrl
import com.forum.frontend.data.remote.dto.NotificationDto
import com.forum.frontend.domain.model.Notification
import com.forum.frontend.domain.model.NotificationType

fun NotificationDto.toDomain(): Notification = Notification(
    id = id,
    type = NotificationType.fromApi(type),
    isRead = isRead,
    createdAt = parseApiDate(createdAt),
    actorId = actorId,
    actorFirstName = actorFirstName.orEmpty(),
    actorLastName = actorLastName.orEmpty(),
    actorPictureUrl = actorPictureUrl.toSafeUrl(),
    postId = postId,
    commentId = commentId,
    badgeName = badgeName,
    badgeIconUrl = badgeIconUrl.toSafeUrl(),
    postPreview = postPreview?.takeIf { it.isNotBlank() },
)
