package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.CurrentUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<CurrentUser?>
    suspend fun login(carnet: String, password: String): CurrentUser
    suspend fun logout()
}