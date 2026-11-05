package com.forum.frontend.core

fun String?.toSafeUrl(): String? {
    val value = this?.trim().orEmpty()
    if (value.isEmpty()) return null
    val lower = value.lowercase()
    val allowed = lower.startsWith("https://") || lower.startsWith("http://") ||
        (value.startsWith("/") && !value.startsWith("//"))
    return value.takeIf { allowed }
}
