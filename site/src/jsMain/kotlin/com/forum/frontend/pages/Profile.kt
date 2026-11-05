package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.profile.ProfileScreen
import com.forum.frontend.presentation.profile.ProfileViewModel
import com.forum.frontend.presentation.share.rememberShareController
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun ProfilePage() {
    val ctx = rememberPageContext()
    val requestedId = ctx.route.params["id"]?.toIntOrNull()
    val viewModel = rememberViewModel<ProfileViewModel>()
    val shareController = rememberShareController()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(requestedId) {
        viewModel.load(requestedId)
    }

    LaunchedEffect(state.profile?.fullName) {
        document.title = "${state.profile?.fullName ?: "Perfil"} · Forum"
    }

    ProfileScreen(
        state = state,
        onTabSelect = viewModel::selectTab,
        onPostOpen = { id -> ctx.router.navigateTo("/post?id=$id") },
        onLikeClick = viewModel::toggleLike,
        onComposeClick = { ctx.router.navigateTo("/new") },
        onShareClick = shareController::open,
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadMore,
        onDismissMessage = viewModel::dismissMessage,
    )
}