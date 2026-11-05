package com.forum.frontend.presentation.newpost

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.UploadRules
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.domain.repository.UploadRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.w3c.dom.url.URL
import org.w3c.files.File

class NewPostViewModel(
    private val postRepository: PostRepository,
    private val uploadRepository: UploadRepository,
    authRepository: AuthRepository,
    private val loading: LoadingTracker,
) : ViewModel() {

    private val spaces = authRepository.currentUser.value?.spaces.orEmpty()
    private var nextKey = 0

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

    fun addFiles(files: List<File>) {
        val free = UploadRules.MAX_FILES - _state.value.attachments.size
        if (files.size > free) {
            _state.update { it.copy(error = "Máximo ${UploadRules.MAX_FILES} archivos por publicación") }
        }
        files.take(free.coerceAtLeast(0)).forEach(::addFile)
    }

    private fun addFile(file: File) {
        UploadRules.check(file.type, file.size.toDouble())?.let { message ->
            _state.update { it.copy(error = message) }
            return
        }
        val type = UploadRules.typeOf(file.type) ?: return
        val attachment = Attachment(key = nextKey++, previewUrl = URL.createObjectURL(file), type = type)
        _state.update { it.copy(attachments = it.attachments + attachment, error = null) }
        scope.launch {
            resultOf { uploadRepository.upload(file) }
                .onSuccess { uploaded ->
                    _state.update { s ->
                        s.copy(attachments = s.attachments.map { if (it.key == attachment.key) it.copy(uploadId = uploaded.id) else it })
                    }
                }
                .onFailure { error ->
                    URL.revokeObjectURL(attachment.previewUrl)
                    _state.update { s ->
                        s.copy(attachments = s.attachments.filterNot { it.key == attachment.key }, error = error.toUserMessage())
                    }
                }
        }
    }

    fun removeAttachment(key: Int) {
        val target = _state.value.attachments.firstOrNull { it.key == key } ?: return
        URL.revokeObjectURL(target.previewUrl)
        _state.update { s -> s.copy(attachments = s.attachments.filterNot { it.key == key }) }
        target.uploadId?.let { id -> scope.launch { resultOf { uploadRepository.discard(id) } } }
    }

    fun submit() {
        val current = _state.value
        val spaceId = current.selectedSpaceId ?: return
        if (!current.canSubmit) return
        val mediaIds = current.attachments.mapNotNull { it.uploadId }
        _state.update { it.copy(isSubmitting = true, error = null) }
        scope.launch {
            resultOf {
                loading.blocking("Publicando") {
                    postRepository.createPost(spaceId, current.content.trim(), mediaIds)
                }
            }
                .onSuccess { _state.update { it.copy(isSubmitting = false, isPublished = true) } }
                .onFailure { error -> _state.update { it.copy(isSubmitting = false, error = error.toUserMessage()) } }
        }
    }

    override fun onCleared() {
        _state.value.attachments.forEach { URL.revokeObjectURL(it.previewUrl) }
        super.onCleared()
    }
}
