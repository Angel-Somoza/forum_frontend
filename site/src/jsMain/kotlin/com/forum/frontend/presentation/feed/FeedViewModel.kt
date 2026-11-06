package com.forum.frontend.presentation.feed

import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.presentation.core.ViewModel
import com.forum.frontend.presentation.share.ShareController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val postRepository: PostRepository,
    authRepository: AuthRepository,
    shareController: ShareController,
) : ViewModel() {

    private val spaces = authRepository.currentUser.value?.spaces.orEmpty()
    private val currentUserId = authRepository.currentUser.value?.id
    private val isStaff = authRepository.currentUser.value?.isStaff == true

    private val _state = MutableStateFlow(
        FeedUiState(spaces = spaces, showSpaceFilter = spaces.size > 1),
    )
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    private var loadJob: Job? = null
    private val pendingLikes = mutableSetOf<Int>()

    init {
        loadFirstPage()
        scope.launch {
            shareController.shared.collect { created -> onShared(created) }
        }
    }

    private fun onShared(created: Post) {
        _state.update { s ->
            val counted = s.posts.map {
                if (it.id == created.sharedPostId) it.copy(shareCount = it.shareCount + 1) else it
            }
            val visible = s.selectedSpaceId == null || s.selectedSpaceId == created.spaceId
            s.copy(posts = if (visible && counted.none { it.id == created.id }) listOf(created) + counted else counted)
        }
    }

    fun selectSpace(spaceId: Int?) {
        if (spaceId == _state.value.selectedSpaceId) return
        _state.update { it.copy(selectedSpaceId = spaceId) }
        loadFirstPage()
    }

    fun retry() {
        loadFirstPage()
    }

    fun loadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        _state.update { it.copy(isLoadingMore = true) }
        loadJob = scope.launch {
            resultOf { postRepository.getFeed(current.page + 1, current.selectedSpaceId) }
                .onSuccess { result ->
                    _state.update { s ->
                        s.copy(
                            posts = s.posts + result.items.filter { new -> s.posts.none { old -> old.id == new.id } },
                            page = result.page,
                            hasMore = result.hasMore,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { error -> _state.update { it.copy(isLoadingMore = false, message = error.toUserMessage()) } }
        }
    }

    fun toggleLike(postId: Int) {
        if (postId in pendingLikes) return
        val original = _state.value.posts.firstOrNull { it.id == postId } ?: return
        val wasLiked = original.isLiked
        replacePost(
            original.copy(
                myReaction = if (wasLiked) null else ReactionType.LIKE,
                reactionCount = original.reactionCount + if (wasLiked) -1 else 1,
            ),
        )
        pendingLikes += postId
        scope.launch {
            resultOf {
                if (wasLiked) postRepository.removeReaction(postId) else postRepository.react(postId, ReactionType.LIKE)
            }.onFailure { error ->
                replacePost(original)
                _state.update { it.copy(message = error.toUserMessage()) }
            }
            pendingLikes -= postId
        }
    }

    fun canDelete(post: Post): Boolean = isStaff || post.author.id == currentUserId

    fun deletePost(postId: Int) {
        val current = _state.value.posts
        val target = current.firstOrNull { it.id == postId } ?: return
        if (!canDelete(target)) return
        _state.update { s -> s.copy(posts = s.posts.filterNot { it.id == postId }) }
        scope.launch {
            resultOf { postRepository.deletePost(postId) }
                .onSuccess { _state.update { it.copy(message = "Publicación eliminada") } }
                .onFailure { error ->
                    _state.update { s ->
                        val index = current.indexOfFirst { it.id == postId }.coerceAtMost(s.posts.size)
                        s.copy(posts = s.posts.toMutableList().apply { add(index, target) }, message = error.toUserMessage())
                    }
                }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(message = null) }
    }

    private fun loadFirstPage() {
        loadJob?.cancel()
        val spaceId = _state.value.selectedSpaceId
        _state.update { it.copy(isLoading = true, error = null, posts = emptyList(), page = 0, hasMore = false) }
        loadJob = scope.launch {
            resultOf { postRepository.getFeed(1, spaceId) }
                .onSuccess { result ->
                    _state.update {
                        it.copy(isLoading = false, posts = result.items, page = result.page, hasMore = result.hasMore)
                    }
                }
                .onFailure { error -> _state.update { it.copy(isLoading = false, error = error.toUserMessage()) } }
        }
    }

    private fun replacePost(post: Post) {
        _state.update { s -> s.copy(posts = s.posts.map { if (it.id == post.id) post else it }) }
    }
}