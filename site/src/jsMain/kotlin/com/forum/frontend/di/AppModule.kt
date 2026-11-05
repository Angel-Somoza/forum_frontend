package com.forum.frontend.di

import com.forum.frontend.core.AppConfig
import com.forum.frontend.data.remote.ApiClient
import com.forum.frontend.data.repository.AuthRepositoryImpl
import com.forum.frontend.data.repository.NotificationRepositoryImpl
import com.forum.frontend.data.repository.PostRepositoryImpl
import com.forum.frontend.data.session.SessionStore
import com.forum.frontend.domain.repository.AuthRepository
import com.forum.frontend.domain.repository.NotificationRepository
import com.forum.frontend.domain.repository.PostRepository
import com.forum.frontend.presentation.feed.FeedViewModel
import com.forum.frontend.presentation.login.LoginViewModel
import com.forum.frontend.presentation.newpost.NewPostViewModel
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
}

val presentationModule = module {
    factory { LoginViewModel(get()) }
    factory { AppShellViewModel(get(), get()) }
    factory { FeedViewModel(get(), get()) }
    factory { NewPostViewModel(get(), get()) }
}

fun initKoin() {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin { modules(dataModule, presentationModule) }
    }
}