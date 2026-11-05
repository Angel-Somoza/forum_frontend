package com.forum.frontend.data.remote

import com.forum.frontend.core.LoadingTracker
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
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import org.w3c.files.File
import kotlin.coroutines.cancellation.CancellationException

class ApiClient(
    private val baseUrl: String,
    private val session: SessionStore,
    private val loading: LoadingTracker,
) {
    @PublishedApi
    internal val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val http = HttpClient(Js) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend inline fun <reified T> uploadFile(path: String, file: File): T =
        json.decodeFromString<T>(rawUpload(path, file))

    @PublishedApi
    internal suspend fun rawUpload(path: String, file: File): String = loading.request {
        val headers = js("({})")
        headers["Content-Type"] = file.type
        session.token.value?.let { headers["Authorization"] = "Bearer $it" }
        val response = try {
            window.fetch(
                baseUrl + path,
                RequestInit(method = "POST", headers = headers, body = file),
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            throw AppError.Network()
        }
        val text = response.text().await()
        if (!response.ok) {
            val code = resultOf { json.decodeFromString<ErrorResponseDto>(text).error.code }.getOrNull()
            throw errorFor(response.status.toInt(), code)
        }
        text
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
    ): HttpResponse = loading.request {
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
        response
    }

    private suspend fun toAppError(response: HttpResponse): AppError {
        val code = resultOf { response.body<ErrorResponseDto>().error.code }.getOrNull()
        return errorFor(response.status.value, code)
    }

    @PublishedApi
    internal fun errorFor(status: Int, code: String?): AppError {
        return when (status) {
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
            413 -> AppError.TooLarge()
            415 -> AppError.UnsupportedMedia()
            429 -> AppError.RateLimited()
            else -> AppError.Unknown()
        }
    }
}