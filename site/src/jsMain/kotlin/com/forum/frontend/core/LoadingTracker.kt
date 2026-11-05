package com.forum.frontend.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.js.Date

class LoadingTracker {

    private val _pending = MutableStateFlow(0)
    val pending: StateFlow<Int> = _pending.asStateFlow()

    private val _blocking = MutableStateFlow<List<String>>(emptyList())
    val blocking: StateFlow<List<String>> = _blocking.asStateFlow()

    suspend fun <T> request(block: suspend () -> T): T {
        _pending.update { it + 1 }
        try {
            val start = Date.now()
            val outcome = resultOf { block() }
            val wait = MIN_VISIBLE_MS - (Date.now() - start)
            if (wait > 0) delay(wait.toLong())
            return outcome.getOrThrow()
        } finally {
            _pending.update { (it - 1).coerceAtLeast(0) }
        }
    }

    suspend fun <T> blocking(label: String, block: suspend () -> T): T {
        _blocking.update { it + label }
        try {
            return block()
        } finally {
            _blocking.update { it - label }
        }
    }

    private companion object {
        const val MIN_VISIBLE_MS = 650.0
    }
}
