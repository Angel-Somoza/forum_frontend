package com.forum.frontend.domain.model

data class UploadedMedia(val id: String, val type: MediaType, val url: String)

object UploadRules {
    const val MAX_FILES = 10
    private const val MB = 1024 * 1024

    private val limits = mapOf(
        "image/jpeg" to (MediaType.IMAGE to 10 * MB),
        "image/png" to (MediaType.IMAGE to 10 * MB),
        "image/webp" to (MediaType.IMAGE to 10 * MB),
        "image/gif" to (MediaType.GIF to 10 * MB),
        "video/mp4" to (MediaType.VIDEO to 50 * MB),
        "video/webm" to (MediaType.VIDEO to 50 * MB),
    )

    val accept: String = limits.keys.joinToString(",")

    fun typeOf(mime: String): MediaType? = limits[mime]?.first

    fun check(mime: String, size: Double): String? {
        val (_, max) = limits[mime] ?: return "Solo fotos JPG, PNG, WEBP, GIF o videos MP4 y WEBM"
        if (size <= 0) return "El archivo está vacío"
        if (size > max) return "Máximo ${max / MB} MB para este archivo"
        return null
    }
}
