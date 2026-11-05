package com.forum.frontend.domain.validation

import kotlin.js.Date

object AccountRules {
    const val MAX_NAME = 100
    const val MAX_EMAIL = 254
    const val MIN_AGE = 13

    private val carnetRegex = Regex("^\\d{10}$")
    private val nameRegex = Regex("^[\\p{L}][\\p{L} '.-]{0,99}$")
    private val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    private val emailRegex = Regex("^[A-Za-z0-9.!#$%&'*+=?^_`{|}~-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")

    fun carnet(value: String): String? = when {
        value.isBlank() -> "Escribe el carnet"
        !carnetRegex.matches(value.trim()) -> "El carnet debe tener 10 dígitos"
        else -> null
    }

    fun name(value: String, label: String): String? = when {
        value.isBlank() -> "Escribe $label"
        !nameRegex.matches(value.trim()) -> "Usa solo letras en $label"
        else -> null
    }

    fun email(value: String): String? = when {
        value.isBlank() -> null
        value.length > MAX_EMAIL || !emailRegex.matches(value.trim()) -> "Ese correo no se ve bien"
        else -> null
    }

    fun birthDate(value: String): String? {
        if (value.isBlank()) return "Elige la fecha de nacimiento"
        if (!dateRegex.matches(value)) return "Fecha inválida"
        val birth = Date(value + "T00:00:00Z")
        if (birth.getTime().isNaN()) return "Fecha inválida"
        val now = Date()
        val limit = Date(Date.UTC(now.getUTCFullYear() - MIN_AGE, now.getUTCMonth(), now.getUTCDate()))
        if (birth.getTime() > limit.getTime()) return "Debe tener al menos $MIN_AGE años"
        if (birth.getUTCFullYear() < 1900) return "Fecha inválida"
        return null
    }
}
