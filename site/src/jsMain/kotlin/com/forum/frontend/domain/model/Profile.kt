package com.forum.frontend.domain.model

data class ProfileStats(
    val posts: Int,
    val reactions: Int,
    val badges: Int,
)

data class UserProfile(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val pictureUrl: String?,
    val role: Role,
    val stats: ProfileStats,
    val joinedAt: Double? = null,
) {
    val fullName: String get() = "$firstName $lastName"
}

data class Badge(
    val id: Int,
    val name: String,
    val description: String?,
    val iconUrl: String,
    val earnedAt: Double?,
)