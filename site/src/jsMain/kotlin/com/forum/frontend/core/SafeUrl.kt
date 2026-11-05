package com.forum.frontend.core

fun String?.toSafeUrl(): String? {
    val value = this?.trim().orEmpty()
    if (value.isEmpty()) return null
    val lower = value.lowercase()
    return when {
        lower.startsWith("https://") || lower.startsWith("http://") -> value
        value.startsWith("/") && !value.startsWith("//") && !value.contains("..") -> AppConfig.API_ORIGIN + value
        else -> null
    }
}
