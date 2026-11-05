package com.forum.frontend.presentation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.mp.KoinPlatform

abstract class ViewModel {
    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    open fun onCleared() {
        scope.cancel()
    }
}

@Composable
inline fun <reified T : ViewModel> rememberViewModel(): T {
    val viewModel = remember { KoinPlatform.getKoin().get<T>() }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }
    return viewModel
}