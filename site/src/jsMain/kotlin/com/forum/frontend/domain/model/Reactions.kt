package com.forum.frontend.domain.model

fun Post.withToggledLike(): Post = copy(
    myReaction = if (isLiked) null else ReactionType.LIKE,
    reactionCount = (reactionCount + if (isLiked) -1 else 1).coerceAtLeast(0),
)

fun Comment.withToggledLike(): Comment = copy(
    myReaction = if (isLiked) null else ReactionType.LIKE,
    reactionCount = (reactionCount + if (isLiked) -1 else 1).coerceAtLeast(0),
)