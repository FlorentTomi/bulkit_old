package net.asch.bulkit.common.capability

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.drive_network.DriveNetworkHandler
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler

object Capabilities {
    val DISK_MODS: ItemCapability<IItemHandler, Void> =
        ItemCapability.createVoid(BulkIt.location("disk_mods"), IItemHandler::class.java)

    val DISK_STORAGE: BlockCapability<IItemHandler, Void> =
        BlockCapability.createVoid(BulkIt.location("disk_storage"), IItemHandler::class.java)
    val DRIVE_NETWORK: BlockCapability<DriveNetworkHandler, Void> =
        BlockCapability.createVoid(BulkIt.location("drive_network"), DriveNetworkHandler::class.java)

    fun register(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            DRIVE_NETWORK, BlockEntities.DRIVE_NETWORK_VIEW.get(), ::DriveNetworkHandler
        )
    }

}