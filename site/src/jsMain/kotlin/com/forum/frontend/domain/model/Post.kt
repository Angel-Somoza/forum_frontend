package com.forum.frontend.domain.model

data class Author(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val pictureUrl: String?,
    val role: Role,
) {
    val fullName: String get() = "$firstName $lastName"
}

enum class MediaType {
    IMAGE,
    VIDEO,
    GIF;

    companion object {
        fun fromApi(value: String): MediaType? = entries.firstOrNull { it.name == value }
    }
}

data class Media(val id: Int, val type: MediaType, val url: String)

enum class ReactionType {
    LIKE,
    LOVE,
    HAHA,
    WOW,
    SAD,
    ANGRY;

    companion object {
        fun fromApi(value: String): ReactionType? = entries.firstOrNull { it.name == value }
    }
}

data class SharedPost(
    val id: Int,
    val author: Author,
    val spaceName: String?,
    val content: String,
    val createdAt: Double,
    val media: List<Media>,
)

data class Post(
    val id: Int,
    val author: Author,
    val spaceId: Int,
    val spaceName: String,
    val content: String,
    val createdAt: Double,
    val media: List<Media>,
    val reactionCount: Int,
    val myReaction: ReactionType?,
    val commentCount: Int,
    val sharedPost: SharedPost?,
    val sharedPostId: Int? = null,
    val shareCount: Int = 0,
) {
    val isShare: Boolean get() = sharedPostId != null
    val isLiked: Boolean get() = myReaction != null
    val coverMedia: Media? get() = media.firstOrNull { it.type != MediaType.VIDEO }
    val videos: List<Media> get() = media.filter { it.type == MediaType.VIDEO }
}