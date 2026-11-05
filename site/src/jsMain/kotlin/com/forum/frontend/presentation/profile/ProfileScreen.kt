package com.forum.frontend.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.Badge
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserProfile
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.AvatarSize
import com.forum.frontend.ui.components.ChipGroup
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.Toast
import com.forum.frontend.ui.format.compactCount
import com.forum.frontend.ui.format.formatDate
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onTabSelect: (ProfileTab) -> Unit,
    onPostOpen: (Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    when {
        state.isLoading -> LoadingCard()
        state.error != null -> ErrorCard(state.error, onRetry)
        state.profile != null -> {
            val profile = state.profile
            ProfileHeader(profile, state.badges)
            ChipGroup(
                options = ProfileTab.entries.map { tab ->
                    val count = if (tab == ProfileTab.POSTS) profile.stats.posts else profile.stats.badges
                    tab.ordinal to "${tab.label} ($count)"
                },
                selected = state.tab.ordinal,
                onSelect = { index -> if (index != null) onTabSelect(ProfileTab.entries[index]) },
            )
            when (state.tab) {
                ProfileTab.POSTS -> PostsTab(state, onPostOpen, onLoadMore)
                ProfileTab.BADGES -> BadgesTab(state)
            }
        }
    }

    state.message?.let { Toast(it, onDismissMessage) }
}

@Composable
private fun ProfileHeader(profile: UserProfile, badges: List<Badge>) {
    Div({ classes("card", "profile-card") }) {
        Div({ classes("profile-top") }) {
            Avatar(profile.firstName, profile.lastName, profile.pictureUrl, AvatarSize.LARGE)
            Div({ classes("profile-info") }) {
                H1({ classes("profile-name") }) { Text(profile.fullName) }
                Span({ classes("role-chip") }) { Text(profile.role.label) }
                if (badges.isNotEmpty()) BadgeStrip(badges)
            }
        }
        Div({ classes("profile-stats") }) {
            Stat(compactCount(profile.stats.posts), "Publicaciones")
            Stat(compactCount(profile.stats.reactions), "Reacciones")
            Stat(compactCount(profile.stats.badges), "Insignias")
        }
    }
}

@Composable
private fun BadgeStrip(badges: List<Badge>) {
    Div({ classes("badge-strip") }) {
        badges.take(MAX_VISIBLE_BADGES).forEach { badge ->
            Img(src = badge.iconUrl, alt = badge.name, attrs = { title(badge.name) })
        }
        if (badges.size > MAX_VISIBLE_BADGES) {
            Span({ classes("badge-more") }) { Text("+${badges.size - MAX_VISIBLE_BADGES}") }
        }
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Div({ classes("stat") }) {
        Span({ classes("stat-value") }) { Text(value) }
        Span({ classes("stat-label") }) { Text(label) }
    }
}

@Composable
private fun PostsTab(state: ProfileUiState, onPostOpen: (Int) -> Unit, onLoadMore: () -> Unit) {
    if (state.posts.isEmpty()) {
        EmptyCard(icon = "grid_view", title = "Sin publicaciones")
        return
    }
    Div({ classes("post-grid") }) {
        state.posts.forEach { post ->
            key(post.id) { PostTile(post, onOpen = { onPostOpen(post.id) }) }
        }
    }
    if (state.hasMore) {
        Button({
            classes("load-more")
            if (state.isLoadingMore) disabled()
            onClick { onLoadMore() }
        }) { Text(if (state.isLoadingMore) "Cargando..." else "Ver más") }
    }
}

@Composable
private fun PostTile(post: Post, onOpen: () -> Unit) {
    Button({
        classes("post-tile")
        title(post.content.take(80))
        onClick { onOpen() }
    }) {
        val cover = post.coverMedia
        if (cover != null) {
            Img(src = cover.url, alt = "", attrs = {
                classes("post-tile-image")
                attr("loading", "lazy")
            })
        } else {
            Span({ classes("post-tile-text") }) { Text(post.content.take(160)) }
        }
        Span({ classes("post-tile-stats") }) {
            Icon("favorite")
            Text(compactCount(post.reactionCount))
        }
    }
}

@Composable
private fun BadgesTab(state: ProfileUiState) {
    if (state.badges.isEmpty()) {
        EmptyCard(icon = "workspace_premium", title = "Sin insignias todavía")
        return
    }
    Div({ classes("card", "badge-list") }) {
        state.badges.forEach { badge ->
            key(badge.id) {
                Div({ classes("badge-row") }) {
                    Img(src = badge.iconUrl, alt = badge.name, attrs = { classes("badge-icon") })
                    Div({ classes("person-text") }) {
                        Span({ classes("author-name") }) { Text(badge.name) }
                        badge.description?.let { Span({ classes("author-meta") }) { Text(it) } }
                        badge.earnedAt?.let { Span({ classes("author-meta") }) { Text(formatDate(it)) } }
                    }
                }
            }
        }
    }
}

private const val MAX_VISIBLE_BADGES = 5