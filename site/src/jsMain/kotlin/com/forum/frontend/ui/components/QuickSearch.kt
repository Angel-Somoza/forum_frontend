package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.forum.frontend.domain.model.Post
import com.forum.frontend.presentation.core.rememberViewModel
import com.forum.frontend.presentation.search.PersonResult
import com.forum.frontend.presentation.search.SearchField
import com.forum.frontend.presentation.search.SearchViewModel
import io.ktor.http.encodeURLParameter
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun QuickSearch(onNavigate: (String) -> Unit) {
    val viewModel = rememberViewModel<SearchViewModel>()
    remember { viewModel.useCompactPreview() }
    val state by viewModel.state.collectAsState()
    var open by remember { mutableStateOf(false) }

    fun go(path: String) {
        open = false
        viewModel.clear()
        onNavigate(path)
    }

    fun seeAll() {
        val term = state.query.trim()
        if (term.length >= 2) go("/search?q=${term.encodeURLParameter()}")
    }

    Div({ classes("quick-search") }) {
        if (open) Div({
            classes("quick-search-backdrop")
            onClick { open = false }
        })
        SearchField(
            inputId = "quick-search-input",
            query = state.query,
            isSearching = state.isLoading,
            onQueryChange = {
                open = true
                viewModel.onQueryChange(it)
            },
            onClearClick = viewModel::clear,
            onFocus = { open = true },
            onSubmit = ::seeAll,
            onEscape = { open = false },
        )
        if (open && !state.isQueryTooShort) {
            Div({
                classes("quick-panel")
                attr("role", "listbox")
            }) {
                when {
                    state.error != null -> Div({ classes("quick-empty") }) { Text(state.error!!) }
                    state.isEmptyResult -> Div({ classes("quick-empty") }) {
                        Icon("search_off")
                        Span { Text("Sin resultados") }
                    }
                    !state.hasSearched -> Div({ classes("quick-empty") }) { Div({ classes("spinner", "small") }) }
                    else -> {
                        if (state.people.isNotEmpty()) {
                            Span({ classes("quick-label") }) { Text("Personas") }
                            state.people.forEach { person ->
                                key("u${person.id}") { PersonResult(person, onOpen = { go("/profile?id=${person.id}") }) }
                            }
                        }
                        if (state.posts.isNotEmpty()) {
                            Span({ classes("quick-label") }) { Text("Publicaciones") }
                            state.posts.forEach { post ->
                                key("p${post.id}") { QuickPost(post, onOpen = { go("/post?id=${post.id}") }) }
                            }
                        }
                    }
                }
                Button({
                    classes("quick-all")
                    onClick { seeAll() }
                }) {
                    Icon("search")
                    Span { Text("Ver todos los resultados de \"${state.query.trim()}\"") }
                }
            }
        }
    }
}

@Composable
private fun QuickPost(post: Post, onOpen: () -> Unit) {
    Button({
        classes("person-row", "quick-post")
        onClick { onOpen() }
    }) {
        Avatar(post.author.firstName, post.author.lastName, post.author.pictureUrl, AvatarSize.SMALL)
        Div({ classes("person-text") }) {
            Span({ classes("author-name") }) { Text(post.author.fullName) }
            Span({ classes("author-meta", "single-line") }) {
                Text(post.content.ifBlank { if (post.media.isNotEmpty()) "Publicación con archivos" else "" })
            }
        }
        if (post.media.isNotEmpty()) Icon("photo_library")
    }
}
