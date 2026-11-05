package com.forum.frontend.ui.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.shell.AppShellViewModel
import com.forum.frontend.presentation.share.ShareDialog
import com.forum.frontend.ui.components.ScreenLoader
import com.forum.frontend.ui.sections.AppHeader
import com.forum.frontend.ui.sections.BottomNav
import com.forum.frontend.ui.sections.RightRail
import com.forum.frontend.ui.sections.SideNav
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
    val path = ctx.route.path
    val navigate: (String) -> Unit = { target -> ctx.router.navigateTo(target) }

    LaunchedEffect(user) {
        if (user == null) ctx.router.navigateTo("/login")
    }

    LaunchedEffect(path) {
        viewModel.refreshUnreadCount()
    }

    val currentUser = user ?: run {
        ScreenLoader()
        return
    }

    Div({ classes("app-shell") }) {
        TopBar(user = currentUser, onNavigate = navigate)
        AppHeader(
            user = currentUser,
            onProfileClick = { navigate("/profile") },
            onAdminClick = if (currentUser.isStaff) ({ navigate("/admin") }) else null,
            onLogoutClick = viewModel::logout,
        )
        Div({ classes("app-body") }) {
            SideNav(
                currentPath = path,
                isStaff = currentUser.isStaff,
                unreadCount = unreadCount,
                onNavigate = navigate,
                onLogoutClick = viewModel::logout,
            )
            Main({ classes("app-content") }) {
                content()
            }
            RightRail(user = currentUser, currentPath = path, onNavigate = navigate)
        }
        BottomNav(
            currentPath = path,
            unreadCount = unreadCount,
            onNavigate = navigate,
            onCreateClick = { navigate("/new") },
        )
        ShareDialog()
    }
}
