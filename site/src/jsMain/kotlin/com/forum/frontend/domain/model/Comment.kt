package com.forum.frontend.domain.model

data class Comment(
    val id: Int,
    val postId: Int,
    val author: Author,
    val parentId: Int?,
    val content: String,
    val createdAt: Double,
    val reactionCount: Int,
    val myReaction: ReactionType?,
) {
    val isLiked: Boolean get() = myReaction != null
}

data class CommentNode(
    val comment: Comment,
    val replies: List<CommentNode>,
)

fun List<Comment>.toThread(): List<CommentNode> {
    val ids = map { it.id }.toSet()
    val byParent = groupBy { comment -> comment.parentId?.takeIf { it in ids } }
    fun build(parentId: Int?): List<CommentNode> =
        byParent[parentId].orEmpty().map { CommentNode(it, build(it.id)) }
    return build(null)
}