package com.forum.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadDto(
    @SerialName("upload_id") val id: String,
    @SerialName("media_type") val type: String,
    @SerialName("media_url") val url: String,
)
