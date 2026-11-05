package com.forum.frontend.domain.validation

enum class AccountKind { CARNET, DEVELOPER, USERNAME }

object LoginRules {
    const val MIN_PASSWORD = 8
    const val MIN_DEV_PASSWORD = 1
    const val MAX_PASSWORD = 128
    const val MAX_IDENTIFIER = 32

    private val carnetRegex = Regex("^\\d{10}$")
    private val developerRegex = Regex("^DEV-\\d{4}$", RegexOption.IGNORE_CASE)
    private val usernameRegex = Regex("^[a-z][a-z0-9._]{2,31}$")

    fun normalize(identifier: String): String {
        val value = identifier.trim()
        return when {
            developerRegex.matches(value) -> value.uppercase()
            carnetRegex.matches(value) -> value
            else -> value.lowercase()
        }
    }

    fun kindOf(identifier: String): AccountKind? {
        val value = normalize(identifier)
        return when {
            carnetRegex.matches(value) -> AccountKind.CARNET
            developerRegex.matches(value) -> AccountKind.DEVELOPER
            usernameRegex.matches(value) -> AccountKind.USERNAME
            else -> null
        }
    }

    fun minPasswordFor(kind: AccountKind): Int =
        if (kind == AccountKind.CARNET) MIN_PASSWORD else MIN_DEV_PASSWORD

    fun validate(identifier: String, password: String): String? {
        if (identifier.isBlank()) return "Falta tu carnet"
        val kind = kindOf(identifier) ?: return "Ese carnet no se ve bien"
        if (password.isEmpty()) return "Falta la contraseña"
        if (password.length > MAX_PASSWORD) return "Contraseña demasiado larga"
        val min = minPasswordFor(kind)
        if (password.length < min) return "Mínimo $min caracteres"
        return null
    }
}
