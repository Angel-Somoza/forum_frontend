package com.forum.frontend.ui.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.shell.AppShellViewModel
import com.forum.frontend.ui.sections.Sidebar
import com.forum.frontend.ui.sections.TopBar
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
    var isMenuOpen by remember { mutableStateOf(false) }
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
        Sidebar(
            user = currentUser,
            currentPath = path,
            isOpen = isMenuOpen,
            unreadCount = unreadCount,
            onClose = { isMenuOpen = false },
            onNavigate = { target ->
                isMenuOpen = false
                ctx.router.navigateTo(target)
            },
            onLogout = viewModel::logout,
        )
        Div({ classes("app-main") }) {
            TopBar(
                user = currentUser,
                unreadCount = unreadCount,
                onMenuClick = { isMenuOpen = true },
                onNavigate = { target -> ctx.router.navigateTo(target) },
            )
            Main({ classes("app-content") }) {
                content()
            }
        }
    }
}