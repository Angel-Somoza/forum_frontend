package com.forum.frontend.domain.model

data class Space(val id: Int, val name: String)

enum class Role(val label: String) {
    STUDENT("Estudiante"),
    TEACHER("Catedrático"),
    UNIVERSITY("Universitario"),
    DEVELOPER("Developer"),
    ADMIN("Administrador"),
    UNKNOWN("Usuario");

    companion object {
        fun fromApi(name: String): Role = when (name) {
            "Estudiante" -> STUDENT
            "Catedratico" -> TEACHER
            "Universitario" -> UNIVERSITY
            "Developer" -> DEVELOPER
            "Administrador" -> ADMIN
            else -> UNKNOWN
        }
    }
}

data class CurrentUser(
    val id: Int,
    val carnet: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
    val role: Role,
    val spaces: List<Space>,
) {
    val fullName: String get() = "$firstName $lastName"
    val isDeveloper: Boolean get() = role == Role.DEVELOPER
    val isStaff: Boolean get() = role == Role.DEVELOPER || role == Role.ADMIN
}