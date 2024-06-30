package table

import org.jetbrains.exposed.dao.id.UUIDTable

object ExposableApps : UUIDTable() {
    val protocol = text("protocol")
    val localAddress = text("local_address")
    val localPort = ushort("local_port").nullable()
    val subdomain = text("subdomain")
    val name = text("name")
    val isCustomName = bool("is_custom_name")
    val extraConfig = text("extra_config")
}