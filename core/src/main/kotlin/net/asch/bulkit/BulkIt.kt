package net.asch.bulkit

import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.registry.DeferredResources
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.block.Blocks
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.command.Commands
import net.asch.bulkit.common.command.DiskCommands
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.asch.bulkit.common.menu.Menus
import net.asch.bulkit.network.Payloads
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NewRegistryEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkItApi.ID)
object BulkIt {
    const val ID = BulkItApi.ID
    val LOGGER: Logger = LogManager.getLogger()

    private val CREATIVE_TAB_REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ID)
    private val CREATIVE_TAB_CORE = CREATIVE_TAB_REGISTER.register(ID) { ->
        CreativeModeTab.builder().title(Component.literal("BulkIt")).displayItems(::registerToCreativeTab).build()
    }
    private val CREATIVE_TAB_DISKS = CREATIVE_TAB_REGISTER.register("${ID}_disks") { ->
        CreativeModeTab.builder().title(Component.literal("BulkIt - Disks")).displayItems(::registerDisksToCreativeTab)
            .build()
    }

    init {
        val eventBus = MOD_BUS
        eventBus.addListener(RegisterCapabilitiesEvent::class.java, ::onRegisterCapabilities)
        eventBus.addListener(NewRegistryEvent::class.java, ::onNewRegistry)
        eventBus.addListener(FMLLoadCompleteEvent::class.java, ::onLoadComplete)
        eventBus.addListener(RegisterPayloadHandlersEvent::class.java, Payloads::register)
        register(eventBus)

        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent::class.java, ::onRegisterCommands)
    }

    fun location(path: String): ResourceLocation = BulkItApi.location(path)

    private fun register(eventBus: IEventBus) {
        CREATIVE_TAB_REGISTER.register(eventBus)
        Blocks.register(eventBus)
        BlockEntities.register(eventBus)
        DataComponents.register(eventBus)
        Items.register(eventBus)
        Resources.register(eventBus)
        Menus.register(eventBus)
        Commands.register(eventBus)
    }

    private fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
        Items.registerToCreativeTab(params, output)
        Blocks.registerToCreativeTab(params, output)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun registerDisksToCreativeTab(params: ItemDisplayParameters, output: Output) {
        Resources.addToCreativeTab(output)
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
        LOGGER.info(msg)
    }

    private fun onRegisterCommands(event: RegisterCommandsEvent) {
        DiskCommands.register(event.dispatcher, event.buildContext)
    }
}