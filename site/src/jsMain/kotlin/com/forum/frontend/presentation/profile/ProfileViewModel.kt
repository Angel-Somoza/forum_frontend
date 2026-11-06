package com.forum.frontend.presentation.profile

import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.ReactionType
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.domain.repository.ProfileRepository
import com.forum.frontend.presentation.core.ViewModel
import com.forum.frontend.presentation.share.ShareController
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val postRepository: PostRepository,
    authRepository: AuthRepository,
    shareController: ShareController,
) : ViewModel() {

    private val pendingLikes = mutableSetOf<Int>()

    init {
        scope.launch {
            shareController.shared.collect { created ->
                _state.update { s ->
                    val counted = s.posts.map {
                        if (it.id == created.sharedPostId) it.copy(shareCount = it.shareCount + 1) else it
                    }
                    val mine = s.isOwnProfile && s.posts.none { it.id == created.id }
                    s.copy(posts = if (mine) listOf(created) + counted else counted)
                }
            }
        }
    }

    private val currentUserId = authRepository.currentUser.value?.id
    private val isStaff = authRepository.currentUser.value?.isStaff == true

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var userId: Int? = null
    private var loadJob: Job? = null

    fun load(requestedId: Int?) {
        val id = requestedId ?: currentUserId ?: return
        userId = id
        loadJob?.cancel()
        _state.update { ProfileUiState(isOwnProfile = id == currentUserId, tab = it.tab) }
        loadJob = scope.launch {
            resultOf {
                coroutineScope {
                    val profile = async { profileRepository.getProfile(id) }
                    val badges = async { profileRepository.getBadges(id) }
                    val posts = async { profileRepository.getPosts(id, 1) }
                    Triple(profile.await(), badges.await(), posts.await())
                }
            }.onSuccess { (profile, badges, posts) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profile,
                        badges = badges,
                        posts = posts.items,
                        page = posts.page,
                        hasMore = posts.hasMore,
                    )
                }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.toUserMessage()) }
            }
        }
    }

    fun retry() {
        load(userId)
    }

    fun selectTab(tab: ProfileTab) {
        _state.update { it.copy(tab = tab) }
    }

    fun loadMore() {
        val id = userId ?: return
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        _state.update { it.copy(isLoadingMore = true) }
        scope.launch {
            resultOf { profileRepository.getPosts(id, current.page + 1) }
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

    private fun replacePost(post: Post) {
        _state.update { s -> s.copy(posts = s.posts.map { if (it.id == post.id) post else it }) }
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
}