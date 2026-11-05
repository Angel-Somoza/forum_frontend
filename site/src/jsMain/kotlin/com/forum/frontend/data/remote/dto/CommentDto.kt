package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    @SerialName("comment_id") val id: Int,
    @SerialName("post_id") val postId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("parent_comment_id") val parentId: Int? = null,
    val content: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("author_first_name") val authorFirstName: String,
    @SerialName("author_last_name") val authorLastName: String,
    @SerialName("author_profile_picture_url") val authorPictureUrl: String? = null,
    @SerialName("reaction_count") val reactionCount: Int = 0,
    @SerialName("my_reaction") val myReaction: String? = null,
)

@Serializable
data class CreateCommentRequestDto(
    val content: String,
    @SerialName("parent_comment_id") val parentId: Int? = null,
)