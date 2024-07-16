package net.asch.bulkit

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.DeferredResources
import net.asch.bulkit.api.capability.BulkItCapabilities
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.block.Blocks
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.capability.disk.DiskModHandler
import net.asch.bulkit.common.capability.disk.DiskResourceHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.asch.bulkit.common.network.Payloads
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkIt.ID)
object BulkItCore {
    init {
        val eventBus = MOD_BUS
        eventBus.addListener(RegisterPayloadHandlersEvent::class.java, Payloads::register)
        eventBus.addListener(RegisterCapabilitiesEvent::class.java, ::register)
        eventBus.addListener(NewRegistryEvent::class.java, ::register)
        register(eventBus)
    }

    fun <PayloadType : CustomPacketPayload> sendToServer(payload: PayloadType) = PacketDistributor.sendToServer(payload)

    private fun register(eventBus: IEventBus) {
        Blocks.register(eventBus)
        BlockEntities.register(eventBus)
        DataComponents.register(eventBus)
        Items.register(eventBus)
        Resources.register(eventBus)
    }

    private fun register(event: RegisterCapabilitiesEvent) {
        Capabilities.register(event)
        Resources.registerCapabilities(event)
    }

    private fun register(event: NewRegistryEvent) {
        DeferredResources.register(event)
    }
}