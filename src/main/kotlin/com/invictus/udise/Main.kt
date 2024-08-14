package com.invictus.udise

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.invictus.udise.ui.MainScreenApp

val LocalWindow = staticCompositionLocalOf<ComposeWindow> {
    error("No Window provided")
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "UDISE Parser"
    ) {
        CompositionLocalProvider(
            LocalWindow provides window,
        ) { MainScreenApp() }
    }
}
