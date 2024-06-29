package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class AppSettings(
    val frpcExecutablePath: MutableState<String> = mutableStateOf(""),
    val generalFrpcConfig: MutableState<String> = mutableStateOf(""),
    val proxies: SnapshotStateList<ExposableApp> = mutableStateListOf()
)
