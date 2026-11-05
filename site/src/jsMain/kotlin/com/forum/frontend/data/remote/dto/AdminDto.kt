package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleDto(
    @SerialName("role_id") val id: Int,
    @SerialName("role_name") val name: String,
)

@Serializable
data class AdminUserDto(
    @SerialName("user_id") val id: Int,
    val carnet: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val email: String? = null,
    @SerialName("role_id") val roleId: Int,
    @SerialName("role_name") val roleName: String? = null,
    val status: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class CreateUserRequestDto(
    val carnet: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("date_of_birth") val dateOfBirth: String,
    @SerialName("role_id") val roleId: Int,
    val email: String? = null,
    @SerialName("space_ids") val spaceIds: List<Int>,
)

@Serializable
data class CreatedUserDto(
    @SerialName("user_id") val id: Int,
    val carnet: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("temporary_password") val temporaryPassword: String,
)

@Serializable
data class PasswordResetDto(
    @SerialName("user_id") val id: Int,
    @SerialName("temporary_password") val temporaryPassword: String,
)

@Serializable
data class StatusRequestDto(val status: Boolean)

@Serializable
class EmptyRequestDto
