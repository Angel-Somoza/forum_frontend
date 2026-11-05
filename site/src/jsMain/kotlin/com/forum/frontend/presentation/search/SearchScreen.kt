package com.forum.frontend.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserSummary
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.AvatarSize
import com.forum.frontend.ui.components.ChipGroup
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.PostText
import com.forum.frontend.ui.format.compactCount
import com.forum.frontend.ui.format.timeAgo
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onTabSelect: (SearchTab) -> Unit,
    onPostOpen: (Int) -> Unit,
    onPersonOpen: (Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
) {
    Div({ classes("search-bar") }) {
        Icon("search")
        Input(InputType.Search) {
            id("search-input")
            classes("search-input")
            placeholder("Buscar")
            attr("autocomplete", "off")
            value(state.query)
            onInput { onQueryChange(it.value) }
        }
        if (state.query.isNotEmpty()) {
            Button({
                classes("link-button")
                title("Limpiar")
                onClick { onClearClick() }
            }) { Icon("close") }
        }
    }

    ChipGroup(
        options = SearchTab.entries.map { it.ordinal to it.label },
        selected = state.tab.ordinal,
        onSelect = { index -> if (index != null) onTabSelect(SearchTab.entries[index]) },
    )

    when {
        state.isQueryTooShort -> EmptyCard(icon = "search", title = "Personas o publicaciones")
        state.isLoading -> LoadingCard()
        state.error != null -> ErrorCard(state.error, onRetry)
        state.isEmptyResult -> EmptyCard(icon = "search_off", title = "Sin resultados")
        state.tab == SearchTab.POSTS -> state.posts.forEach { post ->
            key(post.id) { PostResult(post, onOpen = { onPostOpen(post.id) }) }
        }
        else -> Div({ classes("card", "people-list") }) {
            state.people.forEach { person ->
                key(person.id) { PersonResult(person, onOpen = { onPersonOpen(person.id) }) }
            }
        }
    }

    if (!state.isQueryTooShort && !state.isLoading && state.hasMore) {
        Button({
            classes("load-more")
            if (state.isLoadingMore) disabled()
            onClick { onLoadMore() }
        }) { Text(if (state.isLoadingMore) "Cargando..." else "Ver más") }
    }
}

@Composable
private fun PostResult(post: Post, onOpen: () -> Unit) {
    Div({
        classes("card", "result-post")
        onClick { onOpen() }
    }) {
        Div({ classes("post-header") }) {
            Avatar(post.author.firstName, post.author.lastName, post.author.pictureUrl, AvatarSize.SMALL)
            Div({ classes("post-header-text") }) {
                Span({ classes("author-name") }) { Text(post.author.fullName) }
                Span({ classes("author-meta") }) { Text("${post.author.role.label} · ${timeAgo(post.createdAt)}") }
            }
            Span({ classes("space-tag") }) { Text(post.spaceName) }
        }
        if (post.content.isNotBlank()) PostText(post.content, clamp = true)
        Div({ classes("result-meta") }) {
            Icon("favorite")
            Span { Text(compactCount(post.reactionCount)) }
            Icon("chat_bubble")
            Span { Text(compactCount(post.commentCount)) }
        }
    }
}

@Composable
private fun PersonResult(person: UserSummary, onOpen: () -> Unit) {
    Button({
        classes("person-row")
        onClick { onOpen() }
    }) {
        Avatar(person.firstName, person.lastName, person.pictureUrl)
        Div({ classes("person-text") }) {
            Span({ classes("author-name") }) { Text(person.fullName) }
            Span({ classes("author-meta") }) { Text(person.role.label) }
        }
        Icon("chevron_right")
    }
}