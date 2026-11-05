package com.forum.frontend.data.mapper

import com.forum.frontend.data.remote.dto.MeDto
import com.forum.frontend.data.remote.dto.SpaceDto
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.domain.model.Role
import com.forum.frontend.domain.model.Space

fun SpaceDto.toDomain(): Space = Space(id = id, name = name)

fun MeDto.toDomain(): CurrentUser = CurrentUser(
    id = id,
    carnet = carnet,
    firstName = firstName,
    lastName = lastName,
    profilePictureUrl = profilePictureUrl?.takeIf { it.isNotBlank() },
    role = Role.fromApi(roleName),
    spaces = spaces.map { it.toDomain() },
)