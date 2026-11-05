package com.forum.frontend.presentation.share

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.SharedPost
import com.forum.frontend.domain.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShareTarget(val postId: Int, val spaceId: Int, val preview: SharedPost)

data class ShareUiState(
    val target: ShareTarget? = null,
    val comment: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val notice: String? = null,
) {
    val isOpen: Boolean get() = target != null
    val remaining: Int get() = MAX_LENGTH - comment.length

    companion object {
        const val MAX_LENGTH = 5000
    }
}

class ShareController(
    private val postRepository: PostRepository,
    private val loading: LoadingTracker,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(ShareUiState())
    val state: StateFlow<ShareUiState> = _state.asStateFlow()

    private val _shared = MutableSharedFlow<Post>(extraBufferCapacity = 8)
    val shared: SharedFlow<Post> = _shared.asSharedFlow()

    fun open(post: Post) {
        val root = post.sharedPost?.takeIf { post.content.isBlank() && post.media.isEmpty() }
        val target = if (root != null) {
            ShareTarget(root.id, post.spaceId, root)
        } else {
            ShareTarget(
                postId = post.id,
                spaceId = post.spaceId,
                preview = SharedPost(
                    id = post.id,
                    author = post.author,
                    spaceName = post.spaceName,
                    content = post.content,
                    createdAt = post.createdAt,
                    media = post.media,
                ),
            )
        }
        _state.update { ShareUiState(target = target) }
    }

    fun close() {
        if (_state.value.isSubmitting) return
        _state.update { it.copy(target = null, comment = "", error = null) }
    }

    fun onCommentChange(value: String) {
        _state.update { it.copy(comment = value.take(ShareUiState.MAX_LENGTH), error = null) }
    }

    fun dismissNotice() {
        _state.update { it.copy(notice = null) }
    }

    fun submit() {
        val current = _state.value
        val target = current.target ?: return
        if (current.isSubmitting) return
        _state.update { it.copy(isSubmitting = true, error = null) }
        scope.launch {
            resultOf {
                loading.blocking("Compartiendo") {
                    postRepository.createPost(target.spaceId, current.comment.trim(), sharedPostId = target.postId)
                }
            }
                .onSuccess { post ->
                    _state.update { ShareUiState(notice = "Publicación compartida") }
                    _shared.emit(post)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSubmitting = false, error = error.toUserMessage()) }
                }
        }
    }
}
