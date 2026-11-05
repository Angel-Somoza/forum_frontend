package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.ReactionType

interface PostRepository {
    suspend fun getFeed(page: Int, spaceId: Int?): Paged<Post>
    suspend fun getPost(id: Int): Post
    suspend fun createPost(spaceId: Int, content: String, mediaIds: List<String> = emptyList()): Post
    suspend fun deletePost(id: Int)
    suspend fun react(postId: Int, type: ReactionType)
    suspend fun removeReaction(postId: Int)
}