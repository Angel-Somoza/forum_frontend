package com.forum.frontend.domain.model

data class RoleOption(val id: Int, val role: Role, val name: String)

data class ManagedUser(
    val id: Int,
    val carnet: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val role: Role,
    val isActive: Boolean,
) {
    val fullName: String get() = "$firstName $lastName"
}

data class NewAccount(
    val carnet: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String?,
    val roleId: Int,
    val spaceIds: List<Int>,
)

data class IssuedCredential(
    val userId: Int,
    val fullName: String,
    val carnet: String,
    val password: String,
    val isReset: Boolean,
)
