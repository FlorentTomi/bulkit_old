package net.asch.bulkit.common.capability

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.drive_network.DriveNetworkLink
import net.asch.bulkit.common.data.Attachments
import net.minecraft.core.Direction
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler

object Capabilities {
    val DISK_STORAGE: BlockCapability<IItemHandler, Direction?> =
        BlockCapability.createSided(BulkIt.location("disk_storage"), IItemHandler::class.java)

    val DRIVE_NETWORK_LINK: BlockCapability<DriveNetworkLink, Void> =
        BlockCapability.createVoid(BulkIt.location("drive_network_link"), DriveNetworkLink::class.java)

    fun register(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            DISK_STORAGE, BlockEntities.DISK_DRIVE.get()
        ) { stack, _ ->
            stack.getData(Attachments.DRIVE_NETWORK_DISK_STORAGE)

        }
        event.registerBlockEntity(DRIVE_NETWORK_LINK, BlockEntities.DRIVE_NETWORK_VIEW.get(), DriveNetworkLink::build)
    }

}