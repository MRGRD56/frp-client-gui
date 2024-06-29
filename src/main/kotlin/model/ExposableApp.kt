package model

import java.util.*

data class ExposableApp(
    var id: UUID = UUID.randomUUID(),
    var protocol: String = "",
    var localAddress: String = "",
    var localPort: Int? = null,
    var subdomain: String = "",
    var name: String = "",
    var isCustomName: Boolean = false
) {
    fun hasDefaultAddress(): Boolean =
        localAddress.isBlank() || localAddress == "127.0.0.1"

    fun formatShortLocalSocket(): String? {
        val port = localPort ?: return null

        return if (hasDefaultAddress()) {
            port.toString()
        } else {
            "$localAddress:${port}"
        }
    }

    fun getFullDomain(hostname: String): String? =
        if (subdomain.isBlank())
            null
        else
            "$subdomain.$hostname"

    fun getFullUrl(hostname: String, schema: String): String? =
        getFullDomain(hostname)?.let { fullDomain ->
            "$schema://$fullDomain"
        }
}
