package com.forum.frontend.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.search.SearchScreen
import com.forum.frontend.presentation.search.SearchViewModel
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

@Page
@Layout(".ui.layouts.AppLayout")
@Composable
fun SearchPage() {
    val ctx = rememberPageContext()
    val viewModel = rememberViewModel<SearchViewModel>()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        document.title = "Buscar · Forum"
        (document.getElementById("search-input") as? HTMLElement)?.focus()
    }

    SearchScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onClearClick = viewModel::clear,
        onTabSelect = viewModel::selectTab,
        onPostOpen = { id -> ctx.router.navigateTo("/post?id=$id") },
        onPersonOpen = { id -> ctx.router.navigateTo("/profile?id=$id") },
        onRetry = viewModel::retry,
        onLoadMore = viewModel::loadMore,
    )
}