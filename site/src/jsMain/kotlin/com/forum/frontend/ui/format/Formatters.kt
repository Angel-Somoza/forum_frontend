package com.forum.frontend.ui.format

import kotlin.js.Date
import kotlin.math.floor

fun timeAgo(millis: Double): String {
    val seconds = (Date.now() - millis) / 1000
    return when {
        seconds < 60 -> "ahora"
        seconds < 3600 -> "hace ${floor(seconds / 60).toInt()} min"
        seconds < 86400 -> "hace ${floor(seconds / 3600).toInt()} h"
        seconds < 604800 -> "hace ${floor(seconds / 86400).toInt()} d"
        else -> Date(millis).toLocaleDateString("es")
    }
}

fun compactCount(value: Int): String = when {
    value >= 1_000_000 -> "${oneDecimal(value / 1_000_000.0)} M"
    value >= 1_000 -> "${oneDecimal(value / 1_000.0)} K"
    else -> value.toString()
}

private fun oneDecimal(value: Double): String {
    val rounded = floor(value * 10) / 10
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString().replace('.', ',')
}