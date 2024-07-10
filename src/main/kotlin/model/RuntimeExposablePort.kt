package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import model.common.ObservableStringBuffer
import java.util.concurrent.atomic.AtomicReference

class RuntimeExposablePort(
    val app: ExposableApp,
    isRunning: Boolean = false,
    status: RuntimeAppStatus = RuntimeAppStatus.STOPPED,
    val logs: ObservableStringBuffer = ObservableStringBuffer()
) {
    private val job = AtomicReference<Job?>()
    private val process = AtomicReference<Process?>()

    var isRunning by mutableStateOf(isRunning)
    var status by mutableStateOf(status)

    fun start() {
        job.set(CoroutineScope(Dispatchers.IO).launch {
            isRunning = true
            logs.clear()

            try {
                val process = ProcessBuilder(
                    "C:\\_public\\frp_0.52.3_windows_amd64\\frpc.exe",
                    "-c",
                    "C:\\_public\\frp_0.52.3_windows_amd64\\frpc.toml"
                ).apply {
                    environment().apply {
                        put("FRPC_NAME", app.name)
                        put("FRPC_TYPE", app.protocol.lowercase())
                        put("FRPC_IP", app.localAddress)
                        put("FRPC_PORT", app.localPort?.toString())
                        put("FRPC_SUBDOMAIN", app.subdomain)
                    }
                }.start()

                Runtime.getRuntime().addShutdownHook(Thread {
                    if (process.isAlive) {
                        process.destroy()
                        try {
                            process.waitFor()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                })

                this@RuntimeExposablePort.process.set(process)

                status = RuntimeAppStatus.STARTING

                process.inputReader().use { inputReader ->
                    inputReader.forEachLine { line ->
                        when {
                            line.contains("start proxy success") -> status = RuntimeAppStatus.SUCCESS
                            line.contains("start error") -> status = RuntimeAppStatus.FAILED
                        }

                        logs.append(line + '\n')
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
            }
        })
    }

    fun stop() {
        process.get()?.apply {
            if (isAlive) destroy()
            process.set(null)
        }
        job.get()?.apply {
            cancel()
            job.set(null)
        }
        status = RuntimeAppStatus.STOPPED
        isRunning = false
    }

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
