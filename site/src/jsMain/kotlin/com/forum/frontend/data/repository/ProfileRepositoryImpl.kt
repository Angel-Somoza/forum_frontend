package com.forum.frontend.data.repository

import com.forum.frontend.data.mapper.toDomain
import com.forum.frontend.data.mapper.toPaged
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.BadgeDto
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.PostDto
import com.forum.frontend.data.remote.dto.ProfileDto
import com.forum.frontend.domain.model.Badge
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserProfile
import com.forum.frontend.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val api: ApiClient,
) : ProfileRepository {

    override suspend fun getProfile(userId: Int): UserProfile =
        api.get<DataResponse<ProfileDto>>("/users/$userId").data.toDomain()

    override suspend fun getPosts(userId: Int, page: Int): Paged<Post> =
        api.get<PageResponse<PostDto>>("/users/$userId/posts?page=$page&limit=$PAGE_SIZE").toPaged { it.toDomain() }

    override suspend fun getBadges(userId: Int): List<Badge> =
        api.get<PageResponse<BadgeDto>>("/users/$userId/badges?limit=100").data.map { it.toDomain() }

    private companion object {
        const val PAGE_SIZE = 18
    }
}