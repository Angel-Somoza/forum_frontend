package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.Comment
import com.forum.frontend.domain.model.ReactionType

interface CommentRepository {
    suspend fun getComments(postId: Int): List<Comment>
    suspend fun createComment(postId: Int, content: String, parentId: Int?): Comment
    suspend fun deleteComment(commentId: Int)
    suspend fun react(commentId: Int, type: ReactionType)
    suspend fun removeReaction(commentId: Int)
}