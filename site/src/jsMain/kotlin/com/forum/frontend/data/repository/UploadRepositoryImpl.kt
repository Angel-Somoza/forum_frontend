package com.forum.frontend.data.repository

import com.forum.frontend.core.toSafeUrl
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.UploadDto
import com.forum.frontend.domain.error.AppError
import com.forum.frontend.domain.model.MediaType
import com.forum.frontend.domain.model.UploadedMedia
import com.forum.frontend.domain.repository.UploadRepository
import io.ktor.http.HttpMethod
import io.ktor.http.encodeURLPathPart
import org.w3c.files.File

class UploadRepositoryImpl(
    private val api: ApiClient,
) : UploadRepository {

    override suspend fun upload(file: File): UploadedMedia {
        val dto = api.uploadFile<DataResponse<UploadDto>>("/uploads", file).data
        val type = MediaType.fromApi(dto.type) ?: throw AppError.UnsupportedMedia()
        val url = dto.url.toSafeUrl() ?: throw AppError.Unknown()
        return UploadedMedia(id = dto.id, type = type, url = url)
    }

    override suspend fun discard(id: String) {
        api.sendEmpty(HttpMethod.Delete, "/uploads/${id.encodeURLPathPart()}")
    }
}
