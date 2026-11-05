package com.forum.frontend.di

import com.forum.frontend.core.AppConfig
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.repository.AuthRepositoryImpl
import com.forum.frontend.data.repository.CommentRepositoryImpl
import com.forum.frontend.data.repository.NotificationRepositoryImpl
import com.forum.frontend.data.repository.PostRepositoryImpl
import com.forum.frontend.data.repository.ProfileRepositoryImpl
import com.forum.frontend.data.repository.SearchRepositoryImpl
import com.forum.frontend.data.session.SessionStore
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.CommentRepository
import com.forum.frontend.domain.repository.NotificationRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.domain.repository.ProfileRepository
import com.forum.frontend.domain.repository.SearchRepository
import com.forum.frontend.presentation.feed.FeedViewModel
import com.forum.frontend.presentation.login.LoginViewModel
import com.forum.frontend.presentation.newpost.NewPostViewModel
import com.forum.frontend.presentation.notifications.NotificationsViewModel
import com.forum.frontend.presentation.postdetail.PostDetailViewModel
import com.forum.frontend.presentation.profile.ProfileViewModel
import com.forum.frontend.presentation.search.SearchViewModel
import com.forum.frontend.presentation.shell.AppShellViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

val dataModule = module {
    single { SessionStore() }
    single { ApiClient(AppConfig.API_URL, get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<PostRepository> { PostRepositoryImpl(get()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
}

val presentationModule = module {
    factory { LoginViewModel(get()) }
    factory { AppShellViewModel(get(), get()) }
    factory { FeedViewModel(get(), get()) }
    factory { NewPostViewModel(get(), get()) }
    factory { PostDetailViewModel(get(), get(), get()) }
    factory { SearchViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { NotificationsViewModel(get()) }
}

fun initKoin() {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin { modules(dataModule, presentationModule) }
    }
}