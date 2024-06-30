package entity

import model.ExposableApp
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import table.ExposableApps
import java.util.UUID

class ExposableAppEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    var protocol by ExposableApps.protocol
    var localAddress by ExposableApps.localAddress
    var localPort by ExposableApps.localPort
    var subdomain by ExposableApps.subdomain
    var name by ExposableApps.name
    var isCustomName by ExposableApps.isCustomName
    var extraConfig by ExposableApps.extraConfig

    companion object : UUIDEntityClass<ExposableAppEntity>(ExposableApps) {
        fun fromDto(dto: ExposableApp): (entity: ExposableAppEntity) -> Unit = { entity ->
            entity.protocol = dto.protocol
            entity.localAddress = dto.localAddress
            entity.localPort = dto.localPort
            entity.subdomain = dto.subdomain
            entity.name = dto.name
            entity.isCustomName = dto.isCustomName
            entity.extraConfig = dto.extraConfig
        }
    }
}