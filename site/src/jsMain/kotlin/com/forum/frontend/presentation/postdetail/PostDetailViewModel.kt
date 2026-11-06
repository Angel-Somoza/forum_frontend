package com.forum.frontend.presentation.postdetail

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.Comment
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.model.toThread
import com.forum.frontend.domain.model.withToggledLike
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.CommentRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    authRepository: AuthRepository,
    private val loading: LoadingTracker,
) : ViewModel() {

    private val currentUser = authRepository.currentUser.value

    private val _state = MutableStateFlow(
        PostDetailUiState(currentUserId = currentUser?.id, isModerator = currentUser?.isStaff == true),
    )
    val state: StateFlow<PostDetailUiState> = _state.asStateFlow()

    private var postId: Int? = null
    private var comments: List<Comment> = emptyList()
    private val pending = mutableSetOf<String>()

    fun load(id: Int) {
        postId = id
        _state.update { it.copy(isLoading = true, error = null) }
        scope.launch {
            resultOf {
                coroutineScope {
                    val post = async { postRepository.getPost(id) }
                    val list = async { commentRepository.getComments(id) }
                    post.await() to list.await()
                }
            }.onSuccess { (post, list) ->
                comments = list
                _state.update { it.copy(isLoading = false, post = post, thread = list.toThread()) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.toUserMessage()) }
            }
        }
    }

    fun retry() {
        postId?.let { load(it) }
    }

    fun togglePostLike() {
        val original = _state.value.post ?: return
        if (!pending.add(POST_KEY)) return
        _state.update { it.copy(post = original.withToggledLike()) }
        scope.launch {
            resultOf {
                if (original.isLiked) postRepository.removeReaction(original.id)
                else postRepository.react(original.id, ReactionType.LIKE)
            }.onFailure { error ->
                _state.update { it.copy(post = original, message = error.toUserMessage()) }
            }
            pending.remove(POST_KEY)
        }
    }

    fun toggleCommentLike(commentId: Int) {
        val original = comments.firstOrNull { it.id == commentId } ?: return
        val key = "comment-$commentId"
        if (!pending.add(key)) return
        replaceComments(comments.map { if (it.id == commentId) original.withToggledLike() else it })
        scope.launch {
            resultOf {
                if (original.isLiked) commentRepository.removeReaction(commentId)
                else commentRepository.react(commentId, ReactionType.LIKE)
            }.onFailure { error ->
                replaceComments(comments.map { if (it.id == commentId) original else it })
                _state.update { it.copy(message = error.toUserMessage()) }
            }
            pending.remove(key)
        }
    }

    fun onDraftChange(value: String) {
        _state.update { it.copy(draft = value.take(PostDetailUiState.MAX_COMMENT_LENGTH)) }
    }

    fun replyTo(commentId: Int) {
        val target = comments.firstOrNull { it.id == commentId } ?: return
        _state.update { it.copy(replyingTo = target) }
    }

    fun cancelReply() {
        _state.update { it.copy(replyingTo = null) }
    }

    fun sendComment() {
        val current = _state.value
        val id = postId ?: return
        if (!current.canSend) return
        _state.update { it.copy(isSending = true) }
        scope.launch {
            resultOf { commentRepository.createComment(id, current.draft.trim(), current.replyingTo?.id) }
                .onSuccess { created ->
                    replaceComments(comments + created)
                    _state.update { s ->
                        s.copy(
                            isSending = false,
                            draft = "",
                            replyingTo = null,
                            post = s.post?.let { p -> p.copy(commentCount = p.commentCount + 1) },
                        )
                    }
                }
                .onFailure { error -> _state.update { it.copy(isSending = false, message = error.toUserMessage()) } }
        }
    }

    fun deleteComment(commentId: Int) {
        scope.launch {
            resultOf { commentRepository.deleteComment(commentId) }
                .onSuccess {
                    replaceComments(comments.filterNot { it.id == commentId })
                    _state.update { s ->
                        s.copy(
                            post = s.post?.let { p -> p.copy(commentCount = (p.commentCount - 1).coerceAtLeast(0)) },
                            replyingTo = s.replyingTo?.takeIf { it.id != commentId },
                        )
                    }
                }
                .onFailure { error -> _state.update { it.copy(message = error.toUserMessage()) } }
        }
    }

    fun deletePost() {
        val post = _state.value.post ?: return
        scope.launch {
            resultOf { loading.blocking("Eliminando") { postRepository.deletePost(post.id) } }
                .onSuccess { _state.update { it.copy(isDeleted = true) } }
                .onFailure { error -> _state.update { it.copy(message = error.toUserMessage()) } }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(message = null) }
    }

    private fun replaceComments(list: List<Comment>) {
        comments = list
        _state.update { it.copy(thread = list.toThread()) }
    }

    private companion object {
        const val POST_KEY = "post"
    }
}