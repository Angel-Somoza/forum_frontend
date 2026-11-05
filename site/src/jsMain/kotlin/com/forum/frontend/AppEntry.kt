package com.forum.frontend

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.forum.frontend.di.initKoin
import com.varabyte.kobweb.core.App

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    remember { initKoin() }
    content()
}