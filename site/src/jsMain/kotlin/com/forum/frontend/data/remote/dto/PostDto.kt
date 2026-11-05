package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    @SerialName("media_id") val id: Int,
    @SerialName("media_type") val type: String,
    @SerialName("media_url") val url: String,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
data class SharedPostDto(
    @SerialName("post_id") val id: Int,
    val content: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("author_first_name") val authorFirstName: String,
    @SerialName("author_last_name") val authorLastName: String,
    @SerialName("author_profile_picture_url") val authorPictureUrl: String? = null,
)

@Serializable
data class PostDto(
    @SerialName("post_id") val id: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("space_id") val spaceId: Int,
    @SerialName("space_name") val spaceName: String,
    val content: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("author_first_name") val authorFirstName: String,
    @SerialName("author_last_name") val authorLastName: String,
    @SerialName("author_profile_picture_url") val authorPictureUrl: String? = null,
    @SerialName("author_role_name") val authorRoleName: String,
    @SerialName("shared_post") val sharedPost: SharedPostDto? = null,
    val media: List<MediaDto> = emptyList(),
    @SerialName("reaction_count") val reactionCount: Int = 0,
    @SerialName("my_reaction") val myReaction: String? = null,
    @SerialName("comment_count") val commentCount: Int = 0,
)

@Serializable
data class CreatePostRequestDto(
    @SerialName("space_id") val spaceId: Int,
    val content: String? = null,
    val media: List<String>? = null,
)

@Serializable
data class ReactionRequestDto(
    @SerialName("reaction_type") val type: String,
)