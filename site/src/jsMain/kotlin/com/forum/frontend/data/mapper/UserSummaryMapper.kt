package com.forum.frontend.data.mapper

import com.forum.frontend.core.toSafeUrl
import com.forum.frontend.data.remote.dto.UserSummaryDto
import com.forum.frontend.domain.model.Role
import com.forum.frontend.domain.model.UserSummary

fun UserSummaryDto.toDomain(): UserSummary = UserSummary(
    id = id,
    firstName = firstName,
    lastName = lastName,
    pictureUrl = profilePictureUrl.toSafeUrl(),
    role = Role.fromApi(roleName),
)