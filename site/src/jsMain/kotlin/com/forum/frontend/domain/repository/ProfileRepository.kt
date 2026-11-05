package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.Badge
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(userId: Int): UserProfile
    suspend fun getPosts(userId: Int, page: Int): Paged<Post>
    suspend fun getBadges(userId: Int): List<Badge>
}