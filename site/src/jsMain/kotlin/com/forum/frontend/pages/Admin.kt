package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.admin.AdminScreen
import com.forum.frontend.presentation.admin.AdminViewModel
import com.forum.frontend.presentation.core.rememberViewModel
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import kotlinx.browser.document
import kotlinx.browser.window

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun AdminPage() {
    val viewModel = rememberViewModel<AdminViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { document.title = "Administración · Forum" }

    AdminScreen(
        state = state,
        canManage = viewModel::canManage,
        onToggleForm = viewModel::toggleForm,
        onFormChange = viewModel::updateForm,
        onRoleSelect = viewModel::selectRole,
        onSpaceToggle = viewModel::toggleSpace,
        onSubmit = viewModel::submit,
        onCopyPassword = viewModel::copyPassword,
        onDismissCredential = viewModel::dismissCredential,
        onQueryChange = viewModel::onQueryChange,
        onResetPassword = { user ->
            if (window.confirm("¿Generar una nueva contraseña para ${user.fullName}? La actual dejará de funcionar.")) {
                viewModel.resetPassword(user)
            }
        },
        onToggleActive = { user ->
            val action = if (user.isActive) "desactivar" else "activar"
            if (window.confirm("¿Seguro que quieres $action la cuenta de ${user.fullName}?")) viewModel.toggleActive(user)
        },
        onRetryUsers = viewModel::retryUsers,
        onLoadMore = viewModel::loadMore,
        onDismissMessage = viewModel::dismissMessage,
    )
}
