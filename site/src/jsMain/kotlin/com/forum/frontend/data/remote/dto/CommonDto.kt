package com.forum.frontend.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DataResponse<T>(val data: T)

@Serializable
data class PageMetaDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
)

@Serializable
data class PageResponse<T>(val data: List<T>, val meta: PageMetaDto)

@Serializable
data class ErrorDto(val code: String, val message: String)

@Serializable
data class ErrorResponseDto(val error: ErrorDto)