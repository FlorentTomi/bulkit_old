package net.asch.bulkit.common.capability

import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.drive_network.DiskDriveStorage
import net.asch.bulkit.common.capability.drive_network.DriveNetworkLink
import net.minecraft.core.Direction
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler

object Capabilities {
    val DISK_STORAGE: BlockCapability<IItemHandler, Direction?> =
        BlockCapability.createSided(BulkItApi.location("disk_storage"), IItemHandler::class.java)

    fun register(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            DISK_STORAGE, BlockEntities.DISK_DRIVE.get()
        ) { _, _ -> DiskDriveStorage() }

        event.registerBlockEntity(
            Capabilities.DriveNetwork.LINK, BlockEntities.DRIVE_NETWORK_VIEW.get(), DriveNetworkLink::build
        )
    }
}