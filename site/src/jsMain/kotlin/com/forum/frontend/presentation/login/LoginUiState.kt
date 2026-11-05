package com.forum.frontend.presentation.login

data class LoginUiState(
    val carnet: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lockSeconds: Int = 0,
    val isLoggedIn: Boolean = false,
) {
    val isLocked: Boolean get() = lockSeconds > 0

    val canSubmit: Boolean
        get() = carnet.isNotBlank() && password.isNotEmpty() && !isLoading && !isLocked
}
