package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.login.LoginScreen
import com.forum.frontend.presentation.login.LoginViewModel
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document

@Page
@Composable
fun LoginPage() {
    val ctx = rememberPageContext()
    val viewModel = rememberViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        document.title = "Entrar · Forum"
    }

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) ctx.router.navigateTo("/")
    }

    LoginScreen(
        state = state,
        onCarnetChange = viewModel::onCarnetChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::togglePasswordVisibility,
        onLoginClick = viewModel::submit,
    )
}