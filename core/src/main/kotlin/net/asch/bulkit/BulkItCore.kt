package net.asch.bulkit

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.DeferredResources
import net.asch.bulkit.api.ResourceType
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.block.Blocks
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.asch.bulkit.common.network.Payloads
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkIt.ID)
object BulkItCore {
    init {
        val eventBus = MOD_BUS
        eventBus.addListener(RegisterCapabilitiesEvent::class.java, ::onRegisterCapabilities)
        eventBus.addListener(NewRegistryEvent::class.java, ::onNewRegistry)
        eventBus.addListener(FMLLoadCompleteEvent::class.java, ::onLoadComplete)
        eventBus.addListener(RegisterPayloadHandlersEvent::class.java, Payloads::register)
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

    private fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        Capabilities.register(event)
        Resources.registerCapabilities(event)
    }

    private fun onNewRegistry(event: NewRegistryEvent) {
        DeferredResources.register(event)
    }

    private fun onLoadComplete(event: FMLLoadCompleteEvent) {
        val registeredResources = DeferredResources.registeredResources()

        val msg = "registered resources [${
            registeredResources.asSequence().map(ResourceType<*, *, *, *>::key).joinToString(",")
        }]";
        BulkIt.logInfo(msg);
    }
}