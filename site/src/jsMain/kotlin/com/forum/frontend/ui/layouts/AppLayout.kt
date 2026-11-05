package com.forum.frontend.ui.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.shell.AppShellViewModel
import com.forum.frontend.ui.sections.AppHeader
import com.forum.frontend.ui.sections.BottomNav
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Main

@Layout
@Composable
fun AppLayout(content: @Composable () -> Unit) {
    val ctx = rememberPageContext()
    val viewModel = rememberViewModel<AppShellViewModel>()
    val user by viewModel.currentUser.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val path = ctx.route.path

    LaunchedEffect(user) {
        if (user == null) ctx.router.navigateTo("/login")
    }

    LaunchedEffect(path) {
        viewModel.refreshUnreadCount()
    }

    val currentUser = user ?: run {
        Div({ classes("screen-loading") }) { Div({ classes("spinner") }) }
        return
    }

    Div({ classes("app-shell") }) {
        AppHeader(
            user = currentUser,
            onProfileClick = { ctx.router.navigateTo("/profile") },
            onLogoutClick = viewModel::logout,
        )
        Main({ classes("app-content") }) {
            content()
        }
        BottomNav(
            currentPath = path,
            unreadCount = unreadCount,
            onNavigate = { target -> ctx.router.navigateTo(target) },
            onCreateClick = { ctx.router.navigateTo("/new") },
        )
    }
}