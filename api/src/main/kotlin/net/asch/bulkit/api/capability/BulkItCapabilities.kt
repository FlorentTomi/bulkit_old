package net.asch.bulkit.api.capability

import net.asch.bulkit.api.BulkIt
import net.minecraft.core.Direction
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.items.IItemHandler

object BulkItCapabilities {
    object Disk {
        val RESOURCE: ItemCapability<IDiskResourceHandler, Void> = ItemCapability.createVoid(
            BulkIt.location("disk_resource"), IDiskResourceHandler::class.java
        )

        val MODS: ItemCapability<IItemHandler, Void> = ItemCapability.createVoid(
            BulkIt.location("disk_mods"), IItemHandler::class.java
        )
    }

    object DriveNetwork {
        val LINK: BlockCapability<IDriveNetworkLink, Direction?> = BlockCapability.createSided(
            BulkIt.location("drive_network_link"), IDriveNetworkLink::class.java
        )
    }
}