package com.forum.frontend.data.remote

import com.forum.frontend.core.resultOf
import com.forum.frontend.data.remote.dto.ErrorResponseDto
import com.forum.frontend.data.session.SessionStore
import com.forum.frontend.domain.error.AppError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class ApiClient(
    private val baseUrl: String,
    private val session: SessionStore,
) {
    private val http = HttpClient(Js) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend inline fun <reified T> get(path: String): T =
        execute(HttpMethod.Get, path) {}.body()

    suspend inline fun <reified B, reified T> send(method: HttpMethod, path: String, body: B): T =
        execute(method, path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend inline fun <reified B> sendBody(method: HttpMethod, path: String, body: B) {
        execute(method, path) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    suspend fun sendEmpty(method: HttpMethod, path: String) {
        execute(method, path) {}
    }

    @PublishedApi
    internal suspend fun execute(
        method: HttpMethod,
        path: String,
        configure: HttpRequestBuilder.() -> Unit,
    ): HttpResponse {
        val response = try {
            http.request(baseUrl + path) {
                this.method = method
                session.token.value?.let { header(HttpHeaders.Authorization, "Bearer $it") }
                configure()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            throw AppError.Network()
        }
        if (!response.status.isSuccess()) throw toAppError(response)
        return response
    }

    private suspend fun toAppError(response: HttpResponse): AppError {
        val code = resultOf { response.body<ErrorResponseDto>().error.code }.getOrNull()
        return when (response.status.value) {
            400 -> AppError.Validation()
            401 -> if (code == "INVALID_CREDENTIALS") {
                AppError.InvalidCredentials()
            } else {
                session.clear()
                AppError.Unauthorized()
            }
            403 -> AppError.Forbidden()
            404 -> AppError.NotFound()
            409 -> AppError.Conflict()
            429 -> AppError.RateLimited()
            else -> AppError.Unknown()
        }
    }
}