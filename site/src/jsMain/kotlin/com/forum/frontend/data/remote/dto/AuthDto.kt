package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val carnet: String, val password: String)

@Serializable
data class TokenDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_at") val expiresAt: String,
)

@Serializable
data class SpaceDto(
    @SerialName("space_id") val id: Int,
    @SerialName("space_name") val name: String,
)

@Serializable
data class MeDto(
    @SerialName("user_id") val id: Int,
    val carnet: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("profile_picture_url") val profilePictureUrl: String? = null,
    @SerialName("role_name") val roleName: String,
    val spaces: List<SpaceDto> = emptyList(),
)