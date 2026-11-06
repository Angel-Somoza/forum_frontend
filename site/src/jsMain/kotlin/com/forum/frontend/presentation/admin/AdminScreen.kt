package com.forum.frontend.presentation.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.IssuedCredential
import com.forum.frontend.domain.model.ManagedUser
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.Toast
import com.forum.frontend.ui.components.cls
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun AdminScreen(
    state: AdminUiState,
    canManage: (ManagedUser) -> Boolean,
    onToggleForm: () -> Unit,
    onFormChange: ((AccountForm) -> AccountForm) -> Unit,
    onRoleSelect: (Int) -> Unit,
    onSpaceToggle: (Int) -> Unit,
    onSubmit: () -> Unit,
    onCopyPassword: () -> Unit,
    onDismissCredential: () -> Unit,
    onQueryChange: (String) -> Unit,
    onResetPassword: (ManagedUser) -> Unit,
    onToggleActive: (ManagedUser) -> Unit,
    onRetryUsers: () -> Unit,
    onLoadMore: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    if (!state.isAllowed) {
        EmptyCard(icon = "lock", title = "Solo para administración")
        return
    }

    Div({ classes("admin-head") }) {
        Span({ classes("admin-icon") }) { Icon("admin_panel_settings") }
        Div {
            H2({ classes("section-title") }) { Text("Administración") }
            P({ classes("muted") }) { Text("Crea y administra las cuentas del foro") }
        }
    }

    AccountFormCard(state, onToggleForm, onFormChange, onRoleSelect, onSpaceToggle, onSubmit)

    UsersCard(state, canManage, onQueryChange, onResetPassword, onToggleActive, onRetryUsers, onLoadMore)

    state.message?.let { Toast(it, onDismissMessage) }

    state.credential?.let { credential ->
        Div({ classes("modal-backdrop") }) {
            CredentialCard(credential, state.copied, onCopyPassword, onDismissCredential)
        }
    }
}

@Composable
private fun CredentialCard(
    credential: IssuedCredential,
    copied: Boolean,
    onCopy: () -> Unit,
    onDismiss: () -> Unit,
) {
    LaunchedEffect(credential.userId, credential.password) {
        (document.getElementById("credential-done") as? HTMLElement)?.focus()
    }
    Div({
        classes("card", "modal", "credential-card")
        attr("role", "alertdialog")
        attr("aria-modal", "true")
        attr("aria-label", if (credential.isReset) "Nueva contraseña generada" else "Cuenta creada")
    }) {
        Div({ classes("credential-head") }) {
            Span({ classes("credential-icon") }) { Icon(if (credential.isReset) "key" else "how_to_reg") }
            Div {
                Span({ classes("author-name") }) {
                    Text(if (credential.isReset) "Nueva contraseña generada" else "Cuenta creada")
                }
                Span({ classes("author-meta") }) { Text(credential.fullName) }
            }
        }
        Div({ classes("credential-grid") }) {
            Span({ classes("credential-label") }) { Text("Carnet") }
            Code({ classes("credential-value") }) { Text(credential.carnet) }
            Span({ classes("credential-label") }) { Text("Contraseña") }
            Div({ classes("credential-secret") }) {
                Code({ classes("credential-value", "big") }) { Text(credential.password) }
                Button({
                    classes(*cls("btn-ghost", "copy-button", if (copied) "copied" else null))
                    onClick { onCopy() }
                }) {
                    Icon(if (copied) "check" else "content_copy")
                    Span { Text(if (copied) "Copiada" else "Copiar") }
                }
            }
        }
        P({ classes("credential-note") }) {
            Icon("info")
            Span { Text("Solo se muestra esta vez. Entrégala en persona; la persona puede cambiarla después.") }
        }
        Button({
            id("credential-done")
            classes("btn-gold")
            onClick { onDismiss() }
        }) { Text("Listo, ya la entregué") }
    }
}

@Composable
private fun AccountFormCard(
    state: AdminUiState,
    onToggleForm: () -> Unit,
    onFormChange: ((AccountForm) -> AccountForm) -> Unit,
    onRoleSelect: (Int) -> Unit,
    onSpaceToggle: (Int) -> Unit,
    onSubmit: () -> Unit,
) {
    val form = state.form
    val errors = state.errors
    Div({ classes("card", "admin-card") }) {
        Button({
            classes("admin-card-toggle")
            attr("aria-expanded", state.isFormOpen.toString())
            onClick { onToggleForm() }
        }) {
            Icon("person_add")
            Span({ classes("author-name") }) { Text("Nueva cuenta") }
            Span({ classes(*cls("chevron", if (state.isFormOpen) "open" else null)) }) { Icon("expand_more") }
        }
        if (!state.isFormOpen) return@Div

        Div({ classes("form-grid") }) {
            Field("acc-carnet", "Carnet", errors.carnet) {
                Input(InputType.Text) {
                    id("acc-carnet")
                    classes("field-input")
                    placeholder("10 dígitos")
                    attr("inputmode", "numeric")
                    attr("autocomplete", "off")
                    attr("maxlength", "10")
                    value(form.carnet)
                    onInput { e -> onFormChange { it.copy(carnet = e.value.filter(Char::isDigit).take(10)) } }
                }
            }
            Field("acc-email", "Correo (opcional)", errors.email) {
                Input(InputType.Email) {
                    id("acc-email")
                    classes("field-input")
                    placeholder("nombre@correo.com")
                    attr("autocomplete", "off")
                    attr("maxlength", "254")
                    value(form.email)
                    onInput { e -> onFormChange { it.copy(email = e.value) } }
                }
            }
            Field("acc-first", "Nombres", errors.firstName) {
                Input(InputType.Text) {
                    id("acc-first")
                    classes("field-input")
                    attr("autocomplete", "off")
                    attr("maxlength", "100")
                    value(form.firstName)
                    onInput { e -> onFormChange { it.copy(firstName = e.value) } }
                }
            }
            Field("acc-last", "Apellidos", errors.lastName) {
                Input(InputType.Text) {
                    id("acc-last")
                    classes("field-input")
                    attr("autocomplete", "off")
                    attr("maxlength", "100")
                    value(form.lastName)
                    onInput { e -> onFormChange { it.copy(lastName = e.value) } }
                }
            }
            Field("acc-birth", "Fecha de nacimiento", errors.dateOfBirth) {
                Input(InputType.Date) {
                    id("acc-birth")
                    classes("field-input")
                    attr("min", "1900-01-01")
                    value(form.dateOfBirth)
                    onInput { e -> onFormChange { it.copy(dateOfBirth = e.value) } }
                }
            }
        }

        Span({ classes("composer-label") }) { Text("Rol") }
        Div({ classes("chip-group", "wrap") }) {
            state.roles.forEach { option ->
                Button({
                    classes(*cls("chip", if (option.id == form.roleId) "active" else null))
                    onClick { onRoleSelect(option.id) }
                }) { Text(option.role.label) }
            }
        }
        errors.role?.let { P({ classes("form-error") }) { Text(it) } }

        Span({ classes("composer-label") }) { Text("Espacios") }
        Div({ classes("chip-group", "wrap") }) {
            state.spaces.forEach { space ->
                val on = space.id in form.spaceIds
                Button({
                    classes(*cls("chip", if (on) "active" else null))
                    attr("aria-pressed", on.toString())
                    onClick { onSpaceToggle(space.id) }
                }) {
                    if (on) Icon("check")
                    Text(space.name)
                }
            }
        }
        errors.spaces?.let { P({ classes("form-error") }) { Text(it) } }

        Div({ classes("admin-hint") }) {
            Icon("lock")
            Span { Text("La contraseña se genera sola: 8 caracteres al azar con mayúsculas, minúsculas y números.") }
        }
        state.formError?.let { P({ classes("form-error") }) { Text(it) } }
        Div({ classes("composer-actions", "end") }) {
            Button({
                classes("btn-gold")
                if (state.isSubmitting || state.roles.isEmpty()) disabled()
                onClick { onSubmit() }
            }) { Text(if (state.isSubmitting) "Creando..." else "Crear cuenta") }
        }
    }
}

@Composable
private fun Field(id: String, label: String, error: String?, content: @Composable () -> Unit) {
    Div({ classes("form-field") }) {
        Label(forId = id, attrs = { classes("composer-label") }) { Text(label) }
        Div({ classes(*cls("field", if (error != null) "invalid" else null)) }) { content() }
        error?.let { Span({ classes("field-error") }) { Text(it) } }
    }
}

@Composable
private fun UsersCard(
    state: AdminUiState,
    canManage: (ManagedUser) -> Boolean,
    onQueryChange: (String) -> Unit,
    onResetPassword: (ManagedUser) -> Unit,
    onToggleActive: (ManagedUser) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
) {
    Div({ classes("card", "admin-card") }) {
        Div({ classes("admin-card-title") }) {
            Icon("group")
            Span({ classes("author-name") }) { Text("Cuentas") }
        }
        Div({ classes(*cls("search-bar", if (state.isLoadingUsers) "searching" else null)) }) {
            Span({ classes("search-icon") }) { Icon("search") }
            Input(InputType.Search) {
                classes("search-input")
                placeholder("Buscar por nombre o carnet")
                attr("autocomplete", "off")
                attr("maxlength", "100")
                value(state.query)
                onInput { onQueryChange(it.value) }
            }
        }
        when {
            state.usersError != null -> ErrorCard(state.usersError, onRetry)
            !state.isLoadingUsers && state.users.isEmpty() -> P({ classes("muted", "center") }) { Text("Sin cuentas") }
            else -> Div({ classes("account-list") }) {
                state.users.forEach { user ->
                    key(user.id) { AccountRow(user, canManage(user), state.busyUserId == user.id, onResetPassword, onToggleActive) }
                }
            }
        }
        if (state.hasMore) {
            Button({
                classes("load-more")
                if (state.isLoadingMore) disabled()
                onClick { onLoadMore() }
            }) { Text(if (state.isLoadingMore) "Cargando..." else "Ver más") }
        }
    }
}

@Composable
private fun AccountRow(
    user: ManagedUser,
    manageable: Boolean,
    busy: Boolean,
    onResetPassword: (ManagedUser) -> Unit,
    onToggleActive: (ManagedUser) -> Unit,
) {
    Div({ classes(*cls("account-row", if (!user.isActive) "inactive" else null)) }) {
        Avatar(user.firstName, user.lastName, null)
        Div({ classes("person-text") }) {
            Span({ classes("author-name") }) { Text(user.fullName) }
            Span({ classes("author-meta") }) { Text("${user.carnet} · ${user.role.label}") }
        }
        Span({ classes(*cls("status-pill", if (user.isActive) "on" else "off")) }) {
            Text(if (user.isActive) "Activa" else "Inactiva")
        }
        if (manageable) {
            Div({ classes("account-actions") }) {
                Button({
                    classes("round-button", "small")
                    title("Generar nueva contraseña")
                    if (busy) disabled()
                    onClick { onResetPassword(user) }
                }) { Icon("key") }
                Button({
                    classes("round-button", "small")
                    title(if (user.isActive) "Desactivar cuenta" else "Activar cuenta")
                    if (busy) disabled()
                    onClick { onToggleActive(user) }
                }) { Icon(if (user.isActive) "block" else "check_circle") }
            }
        }
    }
}
