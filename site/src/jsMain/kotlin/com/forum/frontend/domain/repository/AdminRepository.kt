package com.forum.frontend.domain.repository

import com.forum.frontend.domain.model.IssuedCredential
import com.forum.frontend.domain.model.ManagedUser
import com.forum.frontend.domain.model.NewAccount
import com.forum.frontend.domain.model.Paged
import com.forum.frontend.domain.model.RoleOption

interface AdminRepository {
    suspend fun roles(): List<RoleOption>
    suspend fun users(page: Int, query: String?): Paged<ManagedUser>
    suspend fun create(account: NewAccount): IssuedCredential
    suspend fun resetPassword(user: ManagedUser): IssuedCredential
    suspend fun setActive(userId: Int, active: Boolean)
}
