package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileStatsDto(
    val posts: Int = 0,
    val reactions: Int = 0,
    val badges: Int = 0,
)

@Serializable
data class ProfileDto(
    @SerialName("user_id") val id: Int,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerialName("role_name") val roleName: String,
    val stats: ProfileStatsDto = ProfileStatsDto(),
)

@Serializable
data class BadgeDto(
    @SerialName("badge_id") val id: Int,
    @SerialName("badge_name") val name: String,
    val description: String? = null,
    @SerialName("icon_url") val iconUrl: String,
    @SerialName("earned_at") val earnedAt: String? = null,
)