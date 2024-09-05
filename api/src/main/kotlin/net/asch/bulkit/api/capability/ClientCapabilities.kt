package net.asch.bulkit.api.capability

import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.registry.ResourceType
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import java.util.function.Supplier

object ClientCapabilities {
    val RESOURCE_RENDERER: ItemCapability<IDiskResourceRenderer, Void?> = ItemCapability.createVoid(
        BulkItApi.location("disk_resource_renderer"), IDiskResourceRenderer::class.java
    )

    fun <T> registerResource(
        event: RegisterCapabilitiesEvent,
        resourceType: Supplier<ResourceType<T>>,
        rendererCapProvider: ICapabilityProvider<ItemStack, Void?, IDiskResourceRenderer>
    ) {
        event.registerItem(RESOURCE_RENDERER, rendererCapProvider, resourceType.get().disk)
    }
}