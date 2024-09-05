package net.asch.bulkit.api.capability

import net.asch.bulkit.api.BulkItApi
import net.minecraft.core.Direction
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.items.IItemHandler

object Capabilities {
    object Disk {
        val RESOURCE: ItemCapability<IDiskResourceHandler, Void?> = ItemCapability.createVoid(
            BulkItApi.location("disk_resource"), IDiskResourceHandler::class.java
        )
        val MODS: ItemCapability<IItemHandler, Void?> = ItemCapability.createVoid(
            BulkItApi.location("disk_mods"), IItemHandler::class.java
        )
    }

    object DriveNetwork {
        var LINK: BlockCapability<IDriveNetworkLink, Direction?> = BlockCapability.createSided(
            BulkItApi.location("drive_network_link"), IDriveNetworkLink::class.java
        )
    }
}