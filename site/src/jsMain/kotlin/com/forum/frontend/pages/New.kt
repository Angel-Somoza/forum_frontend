package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.newpost.NewPostScreen
import com.forum.frontend.presentation.newpost.NewPostViewModel
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun NewPostPage() {
    val ctx = rememberPageContext()
    val viewModel = rememberViewModel<NewPostViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { document.title = "Nueva publicación · Forum" }

    LaunchedEffect(state.isPublished) {
        if (state.isPublished) ctx.router.navigateTo("/")
    }

    NewPostScreen(
        state = state,
        onContentChange = viewModel::onContentChange,
        onSpaceSelect = viewModel::onSpaceSelect,
        onFilesSelected = viewModel::addFiles,
        onRemoveAttachment = viewModel::removeAttachment,
        onPublishClick = viewModel::submit,
        onCancelClick = { ctx.router.navigateTo("/") },
    )
}