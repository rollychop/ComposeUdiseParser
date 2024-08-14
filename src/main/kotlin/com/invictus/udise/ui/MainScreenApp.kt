package com.invictus.udise.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.invictus.udise.ui.screen.home.HomeScreen
import com.invictus.udise.ui.theme.UDISETheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


enum class PermissionState {
    Loading,
    HasPermission,
    NoPermission
}

@Composable
fun MainScreenApp() {
    var permissionState by remember { mutableStateOf(PermissionState.Loading) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            runCatching {
                val url = URL("https://raw.githubusercontent.com/rohit-inv/udise_permission/main/config.json")
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.connect()
                val allow = BufferedReader(InputStreamReader(con.inputStream)).use {
                    val json = Gson().fromJson(it, JsonElement::class.java)
                    json.asJsonObject.getAsJsonPrimitive("allow").asBoolean
                }
                con.disconnect()
                check(allow)
            }.onSuccess {
                permissionState = PermissionState.HasPermission
            }.onFailure {
                permissionState = PermissionState.NoPermission
            }
        }
    }
    UDISETheme {
        Surface {
            when (permissionState) {
                PermissionState.Loading -> LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )

                PermissionState.HasPermission -> HomeScreen()
                PermissionState.NoPermission -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No permission",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colors.error

                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun MainScreenAppPreview() {
    MainScreenApp()
}
