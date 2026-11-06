package com.forum.frontend.core

import kotlinx.browser.document
import kotlinx.browser.window

enum class Environment(val apiOrigin: String) {
    DEV("http://localhost:3000"),
    PROD("https://api.foroudeo.tech"),
}

object AppConfig {
    private val localHosts = setOf("localhost", "127.0.0.1", "[::1]")

    val environment: Environment =
        if (window.location.hostname in localHosts) Environment.DEV else Environment.PROD

    val API_ORIGIN: String = overrideOrigin() ?: environment.apiOrigin

    val API_URL: String = "$API_ORIGIN/api/v1"

    private fun overrideOrigin(): String? {
        val value = document.querySelector("meta[name='forum-api-origin']")?.getAttribute("content")
            ?.trim()?.trimEnd('/')
            .orEmpty()
        val secure = value.startsWith("https://")
        val local = environment == Environment.DEV && value.startsWith("http://")
        return value.takeIf { (secure || local) && !value.contains('@') }
    }
}
