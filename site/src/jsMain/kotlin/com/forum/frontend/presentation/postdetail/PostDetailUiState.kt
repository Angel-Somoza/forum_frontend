package com.forum.frontend.presentation.postdetail

import com.forum.frontend.domain.model.Comment
import com.forum.frontend.domain.model.CommentNode
import com.forum.frontend.domain.model.Post

data class PostDetailUiState(
    val post: Post? = null,
    val thread: List<CommentNode> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val message: String? = null,
    val draft: String = "",
    val replyingTo: Comment? = null,
    val isSending: Boolean = false,
    val isDeleted: Boolean = false,
    val currentUserId: Int? = null,
    val isModerator: Boolean = false,
) {
    val canSend: Boolean get() = draft.isNotBlank() && !isSending

    fun canDelete(authorId: Int): Boolean = isModerator || authorId == currentUserId

    companion object {
        const val MAX_COMMENT_LENGTH = 2000
    }
}