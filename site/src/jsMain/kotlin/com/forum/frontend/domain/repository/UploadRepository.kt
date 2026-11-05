package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.UploadedMedia
import org.w3c.files.File

interface UploadRepository {
    suspend fun upload(file: File): UploadedMedia
    suspend fun discard(id: String)
}
