package net.asch.bulkit

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.registry.DeferredResources
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.block.Blocks
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.asch.bulkit.network.Payloads
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NewRegistryEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkItCore.ID)
object BulkItCore {
    const val ID = BulkIt.ID

    private val CREATIVE_TAB_REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ID)
    private val CREATIVE_TAB = CREATIVE_TAB_REGISTER.register(ID) { ->
        CreativeModeTab.builder().title(Component.literal("BulkIt")).displayItems(::registerToCreativeTab).build()
    }

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
        CREATIVE_TAB_REGISTER.register(eventBus)
        Blocks.register(eventBus)
        BlockEntities.register(eventBus)
        DataComponents.register(eventBus)
        Items.register(eventBus)
        Resources.register(eventBus)
    }

    private fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
        Items.registerToCreativeTab(params, output)
        Blocks.registerToCreativeTab(params, output)
        Resources.registerToCreativeTab(params, output)
    }

    private fun onRegisterCapabilities(event: RegisterCapabilitiesEvent) {
        Capabilities.register(event)
        Resources.registerCapabilities(event)
    }

    private fun onNewRegistry(event: NewRegistryEvent) {
        DeferredResources.register(event)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoadComplete(event: FMLLoadCompleteEvent) {
        val registeredResources = DeferredResources.registeredResources()

        val msg = "registered resources [${
            registeredResources.asSequence().map(ResourceType<*>::key).joinToString(",")
        }]";
        BulkIt.logInfo(msg);
    }
}