package net.asch.bulkit.mekanism.client.capability

import net.asch.bulkit.api.capability.ClientCapabilities
import net.asch.bulkit.mekanism.BulkIt
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

object ClientCapabilities {
    fun register(event: RegisterCapabilitiesEvent) {
        ClientCapabilities.registerResource(event, BulkIt.GAS_RADIOACTIVE, DiskResourceGasRenderer::buildRadioactive)
        ClientCapabilities.registerResource(
            event, BulkIt.GAS_NON_RADIOACTIVE, DiskResourceGasRenderer::buildNonRadioactive
        )
    }
}