package com.forum.frontend.data.repository

import com.forum.frontend.data.mapper.toDomain
import com.forum.frontend.data.mapper.toPaged
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.CreatePostRequestDto
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.PostDto
import com.forum.frontend.data.remote.dto.ReactionRequestDto
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.repository.PostRepository
import io.ktor.http.HttpMethod

class PostRepositoryImpl(
    private val api: ApiClient,
) : PostRepository {

    override suspend fun getFeed(page: Int, spaceId: Int?): Paged<Post> {
        val path = buildString {
            append("/posts?page=$page&limit=$PAGE_SIZE")
            if (spaceId != null) append("&space_id=$spaceId")
        }
        return api.get<PageResponse<PostDto>>(path).toPaged { it.toDomain() }
    }

    override suspend fun getPost(id: Int): Post =
        api.get<DataResponse<PostDto>>("/posts/$id").data.toDomain()

    override suspend fun createPost(spaceId: Int, content: String, mediaIds: List<String>): Post =
        api.send<CreatePostRequestDto, DataResponse<PostDto>>(
            HttpMethod.Post,
            "/posts",
            CreatePostRequestDto(spaceId, content.ifBlank { null }, mediaIds.ifEmpty { null }),
        ).data.toDomain()

    override suspend fun deletePost(id: Int) {
        api.sendEmpty(HttpMethod.Delete, "/posts/$id")
    }

    override suspend fun react(postId: Int, type: ReactionType) {
        api.sendBody(HttpMethod.Put, "/posts/$postId/reaction", ReactionRequestDto(type.name))
    }

    override suspend fun removeReaction(postId: Int) {
        api.sendEmpty(HttpMethod.Delete, "/posts/$postId/reaction")
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}