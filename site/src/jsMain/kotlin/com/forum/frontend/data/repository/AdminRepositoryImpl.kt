package com.forum.frontend.data.repository

import com.forum.frontend.data.mapper.toPaged
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.remote.dto.AdminUserDto
import com.forum.frontend.data.remote.dto.CreateUserRequestDto
import com.forum.frontend.data.remote.dto.CreatedUserDto
import com.forum.frontend.data.remote.dto.DataResponse
import com.forum.frontend.data.remote.dto.EmptyRequestDto
import com.forum.frontend.data.remote.dto.PageResponse
import com.forum.frontend.data.remote.dto.PasswordResetDto
import com.forum.frontend.data.remote.dto.RoleDto
import com.forum.frontend.data.remote.dto.StatusRequestDto
import com.forum.frontend.domain.model.IssuedCredential
import com.forum.frontend.domain.model.ManagedUser
import com.forum.frontend.domain.model.NewAccount
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Role
import com.forum.frontend.domain.model.RoleOption
import com.forum.frontend.domain.repository.AdminRepository
import io.ktor.http.HttpMethod
import io.ktor.http.encodeURLParameter

class AdminRepositoryImpl(
    private val api: ApiClient,
) : AdminRepository {

    private var cachedRoles: List<RoleOption>? = null

    override suspend fun roles(): List<RoleOption> =
        cachedRoles ?: api.get<PageResponse<RoleDto>>("/catalogs/roles?limit=100").data
            .map { RoleOption(id = it.id, role = Role.fromApi(it.name), name = it.name) }
            .also { cachedRoles = it }

    override suspend fun users(page: Int, query: String?): Paged<ManagedUser> {
        val roles = roles().associateBy { it.id }
        val path = buildString {
            append("/admin/users?page=$page&limit=$PAGE_SIZE")
            if (!query.isNullOrBlank()) append("&q=${query.trim().encodeURLParameter()}")
        }
        return api.get<PageResponse<AdminUserDto>>(path).toPaged { dto ->
            ManagedUser(
                id = dto.id,
                carnet = dto.carnet,
                firstName = dto.firstName,
                lastName = dto.lastName,
                email = dto.email,
                role = dto.roleName?.let { Role.fromApi(it) } ?: roles[dto.roleId]?.role ?: Role.UNKNOWN,
                isActive = dto.status,
            )
        }
    }

    override suspend fun create(account: NewAccount): IssuedCredential {
        val created = api.send<CreateUserRequestDto, DataResponse<CreatedUserDto>>(
            HttpMethod.Post,
            "/admin/users",
            CreateUserRequestDto(
                carnet = account.carnet,
                firstName = account.firstName,
                lastName = account.lastName,
                dateOfBirth = account.dateOfBirth,
                roleId = account.roleId,
                email = account.email,
                spaceIds = account.spaceIds,
            ),
        ).data
        return IssuedCredential(
            userId = created.id,
            fullName = "${created.firstName} ${created.lastName}",
            carnet = created.carnet,
            password = created.temporaryPassword,
            isReset = false,
        )
    }

    override suspend fun resetPassword(user: ManagedUser): IssuedCredential {
        val reset = api.send<EmptyRequestDto, DataResponse<PasswordResetDto>>(
            HttpMethod.Post,
            "/admin/users/${user.id}/reset-password",
            EmptyRequestDto(),
        ).data
        return IssuedCredential(
            userId = reset.id,
            fullName = user.fullName,
            carnet = user.carnet,
            password = reset.temporaryPassword,
            isReset = true,
        )
    }

    override suspend fun setActive(userId: Int, active: Boolean) {
        api.sendBody(HttpMethod.Patch, "/admin/users/$userId", StatusRequestDto(active))
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
