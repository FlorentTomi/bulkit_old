package net.asch.bulkit

import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.common.block.Blocks
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.capability.disk.DiskContentFluidHandler
import net.asch.bulkit.common.capability.disk.DiskContentItemHandler
import net.asch.bulkit.common.capability.disk.DiskFluidHandler
import net.asch.bulkit.common.capability.disk.DiskItemHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewFluidHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewItemHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.asch.bulkit.common.network.Payloads
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkIt.ID)
object BulkIt {
    const val ID = "bulkit"
    private val LOGGER = LogManager.getLogger()

    val RESOURCE_ITEM = ResourceHolder.Builder<Item>(DataComponents.REGISTER, Items.REGISTER).build(
        "item",
        BuiltInRegistries.ITEM,
        net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM,
        ::DiskItemHandler,
        ::DiskContentItemHandler,
        net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
        ::DriveNetworkViewItemHandler
    )

    val RESOURCE_FLUID = ResourceHolder.Builder<Fluid>(DataComponents.REGISTER, Items.REGISTER).build(
        "fluid",
        BuiltInRegistries.FLUID,
        net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM,
        ::DiskFluidHandler,
        ::DiskContentFluidHandler,
        net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
        ::DriveNetworkViewFluidHandler
    )

    init {
        val eventBus = MOD_BUS
        eventBus.addListener(RegisterCapabilitiesEvent::class.java, ::registerCapabilities)
        eventBus.addListener(RegisterPayloadHandlersEvent::class.java, Payloads::register)
        register(eventBus)
    }

    fun location(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(ID, path)
    fun logInfo(msg: String) = LOGGER.info("[${ID}]: $msg")

    fun <PayloadType : CustomPacketPayload> sendToServer(payload: PayloadType) = PacketDistributor.sendToServer(payload)

    private fun register(eventBus: IEventBus) {
        Blocks.register(eventBus)
        BlockEntities.register(eventBus)
        DataComponents.register(eventBus)
        Items.register(eventBus)
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        RESOURCE_ITEM.registerCapabilities(event)
        RESOURCE_FLUID.registerCapabilities(event)
        Capabilities.register(event)
    }
}