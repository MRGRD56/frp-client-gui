package model

data class RuntimeExposablePort(
    var app: ExposablePort,
    var isRunning: Boolean = false
)
