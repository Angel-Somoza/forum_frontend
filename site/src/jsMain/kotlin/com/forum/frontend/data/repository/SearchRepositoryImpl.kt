package com.forum.frontend.data.repository

import com.forum.frontend.data.mapper.toDomain
import com.forum.frontend.data.mapper.toPaged
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.PostDto
import com.forum.frontend.data.remote.dto.UserSummaryDto
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserSummary
import com.forum.frontend.domain.repository.SearchRepository
import io.ktor.http.encodeURLParameter

class SearchRepositoryImpl(
    private val api: ApiClient,
) : SearchRepository {

    override suspend fun searchPosts(query: String, page: Int, limit: Int): Paged<Post> =
        api.get<PageResponse<PostDto>>(path("posts", query, page, limit)).toPaged { it.toDomain() }

    override suspend fun searchPeople(query: String, page: Int, limit: Int): Paged<UserSummary> =
        api.get<PageResponse<UserSummaryDto>>(path("users", query, page, limit)).toPaged { it.toDomain() }

    private fun path(type: String, query: String, page: Int, limit: Int): String =
        "/search?type=$type&q=${query.encodeURLParameter()}&page=$page&limit=${limit.coerceIn(1, 50)}"
}