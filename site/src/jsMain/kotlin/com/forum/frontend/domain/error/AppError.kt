package com.forum.frontend.domain.error

sealed class AppError(override val message: String) : Exception(message) {
    class InvalidCredentials : AppError("Carnet o contraseña incorrectos")
    class Unauthorized : AppError("Tu sesión terminó, vuelve a entrar")
    class Forbidden : AppError("No tienes permiso para esto")
    class NotFound : AppError("No lo encontramos")
    class Validation : AppError("Algo no cuadra, revisa los datos")
    class Conflict : AppError("Eso ya existe")
    class RateLimited : AppError("Muchos intentos, espera un momento")
    class TooLarge : AppError("El archivo pesa demasiado")
    class UnsupportedMedia : AppError("Ese tipo de archivo no se puede subir")
    class Network : AppError("Sin conexión con el servidor")
    class Unknown : AppError("Algo salió mal")
}

fun Throwable.toUserMessage(): String = (this as? AppError)?.message ?: AppError.Unknown().message