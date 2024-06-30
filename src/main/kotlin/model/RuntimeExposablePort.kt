package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.common.ObservableStringBuffer

class RuntimeExposablePort(
    val app: ExposableApp,
    isRunning: Boolean = false,
    val logs: ObservableStringBuffer = ObservableStringBuffer()
) {
    var isRunning by mutableStateOf(isRunning)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RuntimeExposablePort

        return app == other.app
    }

    override fun hashCode(): Int {
        return app.hashCode()
    }
}
