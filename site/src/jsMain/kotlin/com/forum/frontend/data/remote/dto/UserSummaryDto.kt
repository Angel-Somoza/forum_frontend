package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSummaryDto(
    @SerialName("user_id") val id: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerialName("role_name") val roleName: String,
)