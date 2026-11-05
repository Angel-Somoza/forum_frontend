package com.forum.frontend.presentation.profile

import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.ProfileRepository
import com.forum.frontend.presentation.core.ViewModel
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
    authRepository: AuthRepository,
) : ViewModel() {

    private val currentUserId = authRepository.currentUser.value?.id

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

    fun dismissMessage() {
        _state.update { it.copy(message = null) }
    }
}