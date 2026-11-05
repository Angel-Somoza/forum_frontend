package com.forum.frontend.domain.model

data class UserSummary(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val pictureUrl: String?,
    val role: Role,
) {
    val fullName: String get() = "$firstName $lastName"
}