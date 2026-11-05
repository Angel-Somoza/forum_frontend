package com.forum.frontend.presentation.newpost

import com.forum.frontend.domain.model.Space

data class NewPostUiState(
    val content: String = "",
    val spaces: List<Space> = emptyList(),
    val selectedSpaceId: Int? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isPublished: Boolean = false,
) {
    val remaining: Int get() = MAX_LENGTH - content.length
    val canSubmit: Boolean
        get() = content.isNotBlank() && content.length <= MAX_LENGTH && selectedSpaceId != null && !isSubmitting

    companion object {
        const val MAX_LENGTH = 5000
    }
}