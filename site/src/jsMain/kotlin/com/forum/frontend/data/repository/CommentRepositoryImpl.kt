package com.forum.frontend.data.repository

import com.forum.frontend.data.mapper.toDomain
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.CommentDto
import com.forum.frontend.data.remote.dto.CreateCommentRequestDto
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.ReactionRequestDto
import com.forum.frontend.domain.model.Comment
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.repository.CommentRepository
import io.ktor.http.HttpMethod

class CommentRepositoryImpl(
    private val api: ApiClient,
) : CommentRepository {

    override suspend fun getComments(postId: Int): List<Comment> {
        val comments = mutableListOf<Comment>()
        var page = 1
        do {
            val response = api.get<PageResponse<CommentDto>>("/posts/$postId/comments?page=$page&limit=$PAGE_SIZE")
            comments += response.data.map { it.toDomain() }
            page++
        } while (page <= response.meta.totalPages)
        return comments
    }

    override suspend fun createComment(postId: Int, content: String, parentId: Int?): Comment =
        api.send<CreateCommentRequestDto, DataResponse<CommentDto>>(
            HttpMethod.Post,
            "/posts/$postId/comments",
            CreateCommentRequestDto(content, parentId),
        ).data.toDomain()

    override suspend fun deleteComment(commentId: Int) {
        api.sendEmpty(HttpMethod.Delete, "/comments/$commentId")
    }

    override suspend fun react(commentId: Int, type: ReactionType) {
        api.sendBody(HttpMethod.Put, "/comments/$commentId/reaction", ReactionRequestDto(type.name))
    }

    override suspend fun removeReaction(commentId: Int) {
        api.sendEmpty(HttpMethod.Delete, "/comments/$commentId/reaction")
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}