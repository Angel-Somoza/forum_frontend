package com.forum.frontend.presentation.login

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.AppError
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.validation.LoginRules
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val loading: LoadingTracker,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState(isLoggedIn = authRepository.currentUser.value != null))
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private var failedAttempts = 0

    fun onCarnetChange(value: String) {
        _state.update { it.copy(carnet = value.take(LoginRules.MAX_IDENTIFIER), error = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value.take(LoginRules.MAX_PASSWORD), error = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(showPassword = !it.showPassword) }
    }

    fun submit() {
        val current = _state.value
        if (current.isLoading || current.isLocked) return
        LoginRules.validate(current.carnet, current.password)?.let { message ->
            _state.update { it.copy(error = message) }
            return
        }
        val identifier = LoginRules.normalize(current.carnet)
        _state.update { it.copy(carnet = identifier, isLoading = true, error = null) }
        scope.launch {
            resultOf { loading.blocking("Entrando") { authRepository.login(identifier, current.password) } }
                .onSuccess {
                    failedAttempts = 0
                    _state.update { it.copy(isLoading = false, password = "", isLoggedIn = true) }
                }
                .onFailure { error ->
                    if (error is AppError.InvalidCredentials) failedAttempts++
                    _state.update { it.copy(isLoading = false, password = "", error = error.toUserMessage()) }
                    if (failedAttempts >= MAX_ATTEMPTS || error is AppError.RateLimited) startLock()
                }
        }
    }

    private fun startLock() {
        failedAttempts = 0
        scope.launch {
            for (remaining in LOCK_SECONDS downTo 1) {
                _state.update { it.copy(lockSeconds = remaining, error = "Muchos intentos, espera $remaining s") }
                delay(1000)
            }
            _state.update { it.copy(lockSeconds = 0, error = null) }
        }
    }

    private companion object {
        const val MAX_ATTEMPTS = 5
        const val LOCK_SECONDS = 30
    }
}
