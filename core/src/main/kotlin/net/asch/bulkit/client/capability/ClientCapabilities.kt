package net.asch.bulkit.client.capability

import net.asch.bulkit.api.capability.ClientCapabilities
import net.asch.bulkit.common.Resources
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

object ClientCapabilities {
    fun register(event: RegisterCapabilitiesEvent) {
        ClientCapabilities.registerResource(event, Resources.ITEM, DiskResourceItemRenderer::build)
        ClientCapabilities.registerResource(event, Resources.FLUID, DiskResourceFluidRenderer::build)
        ClientCapabilities.registerResource(event, Resources.ENERGY, DiskResourceEnergyRenderer::build)
    }
}