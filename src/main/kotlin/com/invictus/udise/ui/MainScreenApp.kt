package com.invictus.udise.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.invictus.udise.ui.screen.home.HomeScreen
import com.invictus.udise.ui.theme.UDISETheme

@Composable
fun MainScreenApp() {
    UDISETheme {
        Surface {
            HomeScreen()
        }
    }
}

@Composable
@Preview
fun MainScreenAppPreview() {
    MainScreenApp()
}
