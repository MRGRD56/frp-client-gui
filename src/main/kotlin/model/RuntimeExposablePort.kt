package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class RuntimeExposablePort(
    val app: ExposableApp,
    val isRunning: MutableState<Boolean> = mutableStateOf(false),
    val logs: MutableState<String> = mutableStateOf("")
)
