package net.asch.bulkit.common.block

import net.asch.bulkit.api.BulkIt
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object Blocks {
    private val register = DeferredRegister.createBlocks(BulkIt.ID)

    val DISK_DRIVE: DeferredBlock<DiskDrive> = register.register("disk_drive") { -> DiskDrive() }

    val DRIVE_NETWORK_VIEWS = DriveNetworkView.AVAILABLE_SIZES.associateWith { size ->
        register.register("drive_network_view_$size") { -> DriveNetworkView(size) }
    }

    fun register(eventBus: IEventBus) = register.register(eventBus)
}