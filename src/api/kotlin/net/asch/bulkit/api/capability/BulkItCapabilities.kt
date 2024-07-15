package net.asch.bulkit.api.capability

import net.asch.bulkit.api.BulkIt
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.items.IItemHandler

object BulkItCapabilities {
    object Disk {
        val RESOURCE: ItemCapability<IDiskResourceHandler, Void> = ItemCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(BulkIt.ID, "disk_resource"), IDiskResourceHandler::class.java
        )

        val MODS: ItemCapability<IItemHandler, Void> = ItemCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(BulkIt.ID, "disk_mods"), IItemHandler::class.java
        )
    }
}