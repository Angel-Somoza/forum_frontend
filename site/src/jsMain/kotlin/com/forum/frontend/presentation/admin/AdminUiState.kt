package com.forum.frontend.presentation.admin

import com.forum.frontend.domain.model.IssuedCredential
import com.forum.frontend.domain.model.ManagedUser
import com.forum.frontend.domain.model.RoleOption
import com.forum.frontend.domain.model.Space

data class AccountForm(
    val carnet: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val email: String = "",
    val roleId: Int? = null,
    val spaceIds: Set<Int> = emptySet(),
)

data class FormErrors(
    val carnet: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val email: String? = null,
    val role: String? = null,
    val spaces: String? = null,
) {
    val isEmpty: Boolean
        get() = listOf(carnet, firstName, lastName, dateOfBirth, email, role, spaces).all { it == null }
}

data class AdminUiState(
    val roles: List<RoleOption> = emptyList(),
    val spaces: List<Space> = emptyList(),
    val form: AccountForm = AccountForm(),
    val errors: FormErrors = FormErrors(),
    val isFormOpen: Boolean = true,
    val isSubmitting: Boolean = false,
    val formError: String? = null,
    val credential: IssuedCredential? = null,
    val copied: Boolean = false,
    val query: String = "",
    val users: List<ManagedUser> = emptyList(),
    val isLoadingUsers: Boolean = true,
    val isLoadingMore: Boolean = false,
    val usersError: String? = null,
    val page: Int = 0,
    val hasMore: Boolean = false,
    val busyUserId: Int? = null,
    val message: String? = null,
    val isAllowed: Boolean = false,
)
