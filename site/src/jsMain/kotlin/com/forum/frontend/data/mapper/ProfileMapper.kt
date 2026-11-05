package com.forum.frontend.data.mapper

import com.forum.frontend.data.remote.dto.BadgeDto
import com.forum.frontend.data.remote.dto.ProfileDto
import com.forum.frontend.domain.model.Badge
import com.forum.frontend.domain.model.ProfileStats
import com.forum.frontend.domain.model.Role
import com.forum.frontend.domain.model.UserProfile

fun ProfileDto.toDomain(): UserProfile = UserProfile(
    id = id,
    firstName = firstName,
    lastName = lastName,
    pictureUrl = profilePictureUrl?.takeIf { it.isNotBlank() },
    role = Role.fromApi(roleName),
    stats = ProfileStats(posts = stats.posts, reactions = stats.reactions, badges = stats.badges),
)

fun BadgeDto.toDomain(): Badge = Badge(
    id = id,
    name = name,
    description = description?.takeIf { it.isNotBlank() },
    iconUrl = iconUrl,
    earnedAt = earnedAt?.let { parseApiDate(it) },
)