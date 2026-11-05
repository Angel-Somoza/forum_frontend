package com.forum.frontend.presentation.admin

import com.forum.frontend.core.LoadingTracker
import com.forum.frontend.core.resultOf
import com.forum.frontend.domain.error.AppError
import com.forum.frontend.domain.error.toUserMessage
import com.forum.frontend.domain.model.CurrentUser
import com.forum.frontend.domain.model.ManagedUser
import com.forum.frontend.domain.model.NewAccount
import com.forum.frontend.domain.model.Role
import com.forum.frontend.domain.model.RoleOption
import com.forum.frontend.domain.repository.AdminRepository
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.validation.AccountRules
import com.forum.frontend.presentation.core.ViewModel
import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository,
    authRepository: AuthRepository,
    private val loading: LoadingTracker,
) : ViewModel() {

    private val me: CurrentUser? = authRepository.currentUser.value

    private val _state = MutableStateFlow(
        AdminUiState(isAllowed = me?.isStaff == true, spaces = me?.spaces.orEmpty()),
    )
    val state: StateFlow<AdminUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        if (me?.isStaff == true) {
            loadRoles()
            loadUsers(page = 1)
        }
    }

    private fun loadRoles() {
        scope.launch {
            resultOf { adminRepository.roles() }
                .onSuccess { all ->
                    val assignable = all.filter(::isAssignable)
                    val default = assignable.firstOrNull { it.role == Role.STUDENT } ?: assignable.firstOrNull()
                    _state.update { s ->
                        s.copy(roles = assignable, form = s.form.copy(roleId = default?.id, spaceIds = defaultSpaces(default)))
                    }
                }
                .onFailure { error -> _state.update { it.copy(formError = error.toUserMessage()) } }
        }
    }

    private fun isAssignable(option: RoleOption): Boolean = when (option.role) {
        Role.DEVELOPER, Role.UNKNOWN -> false
        Role.ADMIN -> me?.isDeveloper == true
        else -> true
    }

    private fun defaultSpaces(option: RoleOption?): Set<Int> {
        val spaces = _state.value.spaces
        val wanted = when (option?.role) {
            Role.STUDENT -> "colegio"
            null -> return emptySet()
            else -> "universidad"
        }
        return spaces.filter { it.name.lowercase() == wanted }.map { it.id }.toSet()
            .ifEmpty { spaces.take(1).map { it.id }.toSet() }
    }

    fun toggleForm() {
        _state.update { it.copy(isFormOpen = !it.isFormOpen) }
    }

    fun updateForm(transform: (AccountForm) -> AccountForm) {
        _state.update { it.copy(form = transform(it.form), formError = null) }
    }

    fun selectRole(roleId: Int) {
        val option = _state.value.roles.firstOrNull { it.id == roleId } ?: return
        _state.update { it.copy(form = it.form.copy(roleId = roleId, spaceIds = defaultSpaces(option)), errors = it.errors.copy(role = null)) }
    }

    fun toggleSpace(spaceId: Int) {
        _state.update { s ->
            val ids = s.form.spaceIds
            s.copy(form = s.form.copy(spaceIds = if (spaceId in ids) ids - spaceId else ids + spaceId), errors = s.errors.copy(spaces = null))
        }
    }

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return
        val form = current.form
        val errors = FormErrors(
            carnet = AccountRules.carnet(form.carnet),
            firstName = AccountRules.name(form.firstName, "los nombres"),
            lastName = AccountRules.name(form.lastName, "los apellidos"),
            dateOfBirth = AccountRules.birthDate(form.dateOfBirth),
            email = AccountRules.email(form.email),
            role = if (form.roleId == null) "Elige un rol" else null,
            spaces = if (form.spaceIds.isEmpty()) "Elige al menos un espacio" else null,
        )
        if (!errors.isEmpty) {
            _state.update { it.copy(errors = errors) }
            return
        }
        val account = NewAccount(
            carnet = form.carnet.trim(),
            firstName = form.firstName.trim(),
            lastName = form.lastName.trim(),
            dateOfBirth = form.dateOfBirth,
            email = form.email.trim().ifBlank { null },
            roleId = form.roleId ?: return,
            spaceIds = form.spaceIds.sorted(),
        )
        _state.update { it.copy(isSubmitting = true, errors = FormErrors(), formError = null) }
        scope.launch {
            resultOf { loading.blocking("Creando cuenta") { adminRepository.create(account) } }
                .onSuccess { credential ->
                    _state.update { s ->
                        s.copy(
                            isSubmitting = false,
                            credential = credential,
                            copied = false,
                            form = AccountForm(roleId = form.roleId, spaceIds = form.spaceIds),
                        )
                    }
                    loadUsers(page = 1)
                }
                .onFailure { error ->
                    val message = if (error is AppError.Conflict) "Ya existe una cuenta con ese carnet o correo" else error.toUserMessage()
                    _state.update { it.copy(isSubmitting = false, formError = message) }
                }
        }
    }

    fun copyPassword() {
        val credential = _state.value.credential ?: return
        scope.launch {
            val ok = resultOf { window.navigator.clipboard.writeText(credential.password).await() }.isSuccess
            _state.update { it.copy(copied = ok, message = if (ok) null else "No se pudo copiar, anótala a mano") }
        }
    }

    fun dismissCredential() {
        _state.update { it.copy(credential = null, copied = false) }
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value.take(100)) }
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(300)
            loadUsers(page = 1)
        }
    }

    fun retryUsers() {
        loadUsers(page = 1)
    }

    fun loadMore() {
        val s = _state.value
        if (s.isLoadingUsers || s.isLoadingMore || !s.hasMore) return
        loadUsers(page = s.page + 1)
    }

    private fun loadUsers(page: Int) {
        val query = _state.value.query.trim().takeIf { it.isNotEmpty() }
        _state.update { if (page == 1) it.copy(isLoadingUsers = true, usersError = null) else it.copy(isLoadingMore = true) }
        scope.launch {
            resultOf { adminRepository.users(page, query) }
                .onSuccess { result ->
                    _state.update { s ->
                        s.copy(
                            users = if (page == 1) result.items else s.users + result.items.filter { n -> s.users.none { it.id == n.id } },
                            page = result.page,
                            hasMore = result.hasMore,
                            isLoadingUsers = false,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingUsers = false, isLoadingMore = false, usersError = error.toUserMessage()) }
                }
        }
    }

    fun canManage(user: ManagedUser): Boolean = when {
        user.id == me?.id -> false
        user.role == Role.DEVELOPER -> false
        user.role == Role.ADMIN -> me?.isDeveloper == true
        else -> true
    }

    fun resetPassword(user: ManagedUser) {
        if (!canManage(user) || _state.value.busyUserId != null) return
        _state.update { it.copy(busyUserId = user.id) }
        scope.launch {
            resultOf { loading.blocking("Generando contraseña") { adminRepository.resetPassword(user) } }
                .onSuccess { credential -> _state.update { it.copy(busyUserId = null, credential = credential, copied = false) } }
                .onFailure { error -> _state.update { it.copy(busyUserId = null, message = error.toUserMessage()) } }
        }
    }

    fun toggleActive(user: ManagedUser) {
        if (!canManage(user) || _state.value.busyUserId != null) return
        val active = !user.isActive
        _state.update { it.copy(busyUserId = user.id) }
        scope.launch {
            resultOf { adminRepository.setActive(user.id, active) }
                .onSuccess {
                    _state.update { s ->
                        s.copy(
                            busyUserId = null,
                            users = s.users.map { if (it.id == user.id) it.copy(isActive = active) else it },
                            message = if (active) "Cuenta activada" else "Cuenta desactivada",
                        )
                    }
                }
                .onFailure { error -> _state.update { it.copy(busyUserId = null, message = error.toUserMessage()) } }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(message = null) }
    }

    override fun onCleared() {
        _state.update { it.copy(credential = null) }
        super.onCleared()
    }
}
