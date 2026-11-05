package com.forum.frontend.presentation.login

data class LoginUiState(
    val carnet: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
) {
    val canSubmit: Boolean
        get() = carnet.isNotBlank() && password.isNotBlank() && !isLoading
}