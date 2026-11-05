package com.forum.frontend.data.repository

import com.forum.frontend.core.resultOf
import com.forum.frontend.data.mapper.toDomain
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.LoginRequestDto
import com.forum.frontend.data.remote.dto.MeDto
import com.forum.frontend.data.remote.dto.TokenDto
import com.forum.frontend.data.session.SessionStore
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.domain.repository.AuthRepository
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.StateFlow

class AuthRepositoryImpl(
    private val api: ApiClient,
    private val session: SessionStore,
) : AuthRepository {

    override val currentUser: StateFlow<CurrentUser?> = session.user

    override suspend fun login(carnet: String, password: String): CurrentUser {
        val token = api.send<LoginRequestDto, DataResponse<TokenDto>>(
            HttpMethod.Post,
            "/auth/login",
            LoginRequestDto(carnet, password),
        ).data
        session.open(token.accessToken)
        val user = resultOf { api.get<DataResponse<MeDto>>("/auth/me").data.toDomain() }
            .onFailure { session.clear() }
            .getOrThrow()
        session.setUser(user)
        return user
    }

    override suspend fun logout() {
        try {
            api.sendEmpty(HttpMethod.Post, "/auth/logout")
        } finally {
            session.clear()
        }
    }
}