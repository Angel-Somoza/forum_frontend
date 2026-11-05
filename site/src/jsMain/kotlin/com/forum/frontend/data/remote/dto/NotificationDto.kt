package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnreadCountDto(val count: Int)

@Serializable
data class NotificationDto(
    @SerialName("notification_id") val id: Int,
    @SerialName("notification_type") val type: String,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("actor_user_id") val actorId: Int? = null,
    @SerialName("actor_first_name") val actorFirstName: String? = null,
    @SerialName("actor_last_name") val actorLastName: String? = null,
    @SerialName("actor_profile_picture_url") val actorPictureUrl: String? = null,
    @SerialName("post_id") val postId: Int? = null,
    @SerialName("comment_id") val commentId: Int? = null,
    @SerialName("reaction_type") val reactionType: String? = null,
    @SerialName("badge_name") val badgeName: String? = null,
    @SerialName("badge_icon_url") val badgeIconUrl: String? = null,
    @SerialName("post_preview") val postPreview: String? = null,
)

@Serializable
data class MarkReadRequestDto(
    @SerialName("is_read") val isRead: Boolean,
)