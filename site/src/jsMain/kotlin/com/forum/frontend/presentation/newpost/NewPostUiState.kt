package com.forum.frontend.presentation.newpost

import com.forum.frontend.domain.model.MediaType
import com.forum.frontend.domain.model.Space
import com.forum.frontend.domain.model.UploadRules

data class Attachment(
    val key: Int,
    val previewUrl: String,
    val type: MediaType,
    val uploadId: String? = null,
) {
    val isUploading: Boolean get() = uploadId == null
}

data class NewPostUiState(
    val content: String = "",
    val spaces: List<Space> = emptyList(),
    val selectedSpaceId: Int? = null,
    val attachments: List<Attachment> = emptyList(),
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isPublished: Boolean = false,
) {
    val remaining: Int get() = MAX_LENGTH - content.length
    val isUploading: Boolean get() = attachments.any { it.isUploading }
    val canAttach: Boolean get() = attachments.size < UploadRules.MAX_FILES && !isSubmitting
    val canSubmit: Boolean
        get() = (content.isNotBlank() || attachments.isNotEmpty()) &&
            content.length <= MAX_LENGTH &&
            selectedSpaceId != null &&
            !isUploading &&
            !isSubmitting

    companion object {
        const val MAX_LENGTH = 5000
    }
}
