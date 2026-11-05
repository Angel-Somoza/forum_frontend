package com.forum.frontend.data.session

import com.forum.frontend.domain.model.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionStore {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _user = MutableStateFlow<CurrentUser?>(null)
    val user: StateFlow<CurrentUser?> = _user.asStateFlow()

    fun open(token: String) {
        _token.value = token
    }

    fun setUser(user: CurrentUser) {
        _user.value = user
    }

    fun clear() {
        _token.value = null
        _user.value = null
    }
}