package com.forum.frontend.domain.error

sealed class AppError(override val message: String) : Exception(message) {
    class InvalidCredentials : AppError("Carnet o contraseña incorrectos")
    class Unauthorized : AppError("Tu sesión expiró. Inicia sesión de nuevo")
    class Forbidden : AppError("No tienes permiso para realizar esta acción")
    class NotFound : AppError("No se encontró lo que buscas")
    class Validation : AppError("Revisa los datos ingresados")
    class Conflict : AppError("Ya existe un registro con esos datos")
    class RateLimited : AppError("Demasiados intentos. Espera un minuto e inténtalo de nuevo")
    class Network : AppError("No se pudo conectar con el servidor")
    class Unknown : AppError("Ocurrió un error inesperado")
}

fun Throwable.toUserMessage(): String = (this as? AppError)?.message ?: AppError.Unknown().message