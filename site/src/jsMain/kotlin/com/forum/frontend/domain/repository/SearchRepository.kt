package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.Post
import com.forum.frontend.domain.model.UserSummary

interface SearchRepository {
    suspend fun searchPosts(query: String, page: Int): Paged<Post>
    suspend fun searchPeople(query: String, page: Int): Paged<UserSummary>
}