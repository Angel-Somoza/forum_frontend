package com.forum.frontend.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.forum.frontend.domain.model.Badge
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserProfile
import com.forum.frontend.ui.components.Avatar
import com.forum.frontend.ui.components.AvatarSize
import com.forum.frontend.ui.components.EmptyCard
import com.forum.frontend.ui.components.ErrorCard
import com.forum.frontend.ui.components.Icon
import com.forum.frontend.ui.components.LoadingCard
import com.forum.frontend.ui.components.PostCard
import com.forum.frontend.ui.components.Toast
import com.forum.frontend.ui.components.cls
import com.forum.frontend.ui.format.compactCount
import com.forum.frontend.ui.format.formatDate
import com.forum.frontend.ui.format.formatMonthYear
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.B
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onTabSelect: (ProfileTab) -> Unit,
    onPostOpen: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    onComposeClick: () -> Unit,
    onShareClick: (Post) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    when {
        state.isLoading -> LoadingCard()
        state.error != null -> ErrorCard(state.error, onRetry)
        state.profile != null -> {
            ProfileHero(state.profile, state.badges, state.tab, state.isOwnProfile, onTabSelect, onComposeClick)
            when (state.tab) {
                ProfileTab.POSTS -> PostsTab(state, onPostOpen, onLikeClick, onComposeClick, onShareClick, onLoadMore)
                ProfileTab.BADGES -> BadgesTab(state)
            }
        }
    }

    state.message?.let { Toast(it, onDismissMessage) }
}

@Composable
private fun ProfileHero(
    profile: UserProfile,
    badges: List<Badge>,
    tab: ProfileTab,
    isOwn: Boolean,
    onTabSelect: (ProfileTab) -> Unit,
    onComposeClick: () -> Unit,
) {
    Div({ classes("card", "profile-hero") }) {
        Div({ classes("profile-cover") }) {
            Span({ classes("cover-orb", "orb-a") })
            Span({ classes("cover-orb", "orb-b") })
        }
        Div({ classes("profile-bar") }) {
            Div({ classes("profile-avatar") }) {
                Avatar(profile.firstName, profile.lastName, profile.pictureUrl, AvatarSize.LARGE)
            }
            if (isOwn) {
                Button({
                    classes("btn-gold", "profile-action")
                    onClick { onComposeClick() }
                }) {
                    Icon("edit_square")
                    Span { Text("Publicar") }
                }
            }
        }
        Div({ classes("profile-body") }) {
            H1({ classes("profile-name") }) { Text(profile.fullName) }
            Div({ classes("profile-meta") }) {
                Span({ classes("role-chip") }) { Text(profile.role.label) }
                profile.joinedAt?.let { joined ->
                    Span({ classes("meta-item") }) {
                        Icon("calendar_month")
                        Text("Se unió en ${formatMonthYear(joined)}")
                    }
                }
            }
            if (badges.isNotEmpty()) BadgeStrip(badges)
            Div({ classes("profile-counts") }) {
                Count(profile.stats.posts, "Publicaciones")
                Count(profile.stats.reactions, "Reacciones")
                Count(profile.stats.badges, "Insignias")
            }
        }
        Nav({ classes("profile-tabs") }) {
            ProfileTab.entries.forEach { entry ->
                val active = entry == tab
                Button({
                    classes(*cls("profile-tab", if (active) "active" else null))
                    if (active) attr("aria-current", "page")
                    onClick { onTabSelect(entry) }
                }) { Text(entry.label) }
            }
        }
    }
}

@Composable
private fun Count(value: Int, label: String) {
    Span({ classes("count") }) {
        B { Text(compactCount(value)) }
        Text(" $label")
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
private fun PostsTab(
    state: ProfileUiState,
    onPostOpen: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    onComposeClick: () -> Unit,
    onShareClick: (Post) -> Unit,
    onLoadMore: () -> Unit,
) {
    if (state.posts.isEmpty()) {
        EmptyCard(
            icon = "edit_note",
            title = if (state.isOwnProfile) "Aún no has publicado nada" else "Sin publicaciones todavía",
        )
        if (state.isOwnProfile) {
            Button({
                classes("btn-gold", "profile-empty-action")
                onClick { onComposeClick() }
            }) { Text("Crear mi primera publicación") }
        }
        return
    }
    state.posts.forEach { post ->
        key(post.id) {
            PostCard(
                post = post,
                onLikeClick = { onLikeClick(post.id) },
                onCommentsClick = { onPostOpen(post.id) },
                onShareClick = { onShareClick(post) },
                onOpenPost = onPostOpen,
            )
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
private fun BadgesTab(state: ProfileUiState) {
    if (state.badges.isEmpty()) {
        EmptyCard(icon = "workspace_premium", title = "Sin insignias todavía")
        return
    }
    Div({ classes("badge-grid") }) {
        state.badges.forEach { badge ->
            key(badge.id) {
                Div({ classes("card", "badge-card") }) {
                    Img(src = badge.iconUrl, alt = badge.name, attrs = { classes("badge-icon") })
                    Span({ classes("author-name") }) { Text(badge.name) }
                    badge.description?.let { Span({ classes("author-meta") }) { Text(it) } }
                    badge.earnedAt?.let { Span({ classes("badge-date") }) { Text(formatDate(it)) } }
                }
            }
        }
    }
}

private const val MAX_VISIBLE_BADGES = 6
