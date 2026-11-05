package com.forum.frontend.presentation.newpost

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewPostViewModel(
    private val postRepository: PostRepository,
    authRepository: AuthRepository,
    private val loading: LoadingTracker,
) : ViewModel() {

    private val spaces = authRepository.currentUser.value?.spaces.orEmpty()

    private val _state = MutableStateFlow(
        NewPostUiState(spaces = spaces, selectedSpaceId = spaces.firstOrNull()?.id),
    )
    val state: StateFlow<NewPostUiState> = _state.asStateFlow()

    fun onContentChange(value: String) {
        _state.update { it.copy(content = value.take(NewPostUiState.MAX_LENGTH), error = null) }
    }

    fun onSpaceSelect(spaceId: Int) {
        _state.update { it.copy(selectedSpaceId = spaceId) }
    }

    fun submit() {
        val current = _state.value
        val spaceId = current.selectedSpaceId ?: return
        if (!current.canSubmit) return
        _state.update { it.copy(isSubmitting = true, error = null) }
        scope.launch {
            resultOf { loading.blocking("Publicando") { postRepository.createPost(spaceId, current.content.trim()) } }
                .onSuccess { _state.update { it.copy(isSubmitting = false, isPublished = true) } }
                .onFailure { error -> _state.update { it.copy(isSubmitting = false, error = error.toUserMessage()) } }
        }
    }
}