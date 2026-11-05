package com.forum.frontend.presentation.login

import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState(isLoggedIn = authRepository.currentUser.value != null))
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onCarnetChange(value: String) {
        _state.update { it.copy(carnet = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return
        if (!current.canSubmit) {
            _state.update { it.copy(error = "Ingresa tu carnet y tu contraseña") }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        scope.launch {
            resultOf { authRepository.login(current.carnet.trim(), current.password) }
                .onSuccess { _state.update { it.copy(isLoading = false, isLoggedIn = true) } }
                .onFailure { error -> _state.update { it.copy(isLoading = false, error = error.toUserMessage()) } }
        }
    }
}