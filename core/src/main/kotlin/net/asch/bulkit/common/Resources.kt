package net.asch.bulkit.common

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.registry.DeferredResources
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.client.gui.resource.DiskResourceFluidRenderer
import net.asch.bulkit.client.gui.resource.DiskResourceItemRenderer
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.disk.DiskFluidHandler
import net.asch.bulkit.common.capability.disk.DiskItemHandler
import net.asch.bulkit.common.capability.disk.DiskModHandler
import net.asch.bulkit.common.capability.disk.DiskResourceHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewFluidHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewItemHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab.Output
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredHolder

object Resources {
    private val REGISTER: DeferredResources = DeferredResources(BulkIt.ID)

    val ITEM: DeferredHolder<ResourceType<*>, ResourceType<Item>> = REGISTER.registerResourceType(
        ResourceType.Builder<Item>(
            "item", DataComponents.REGISTER, Items.REGISTER
        ).registry(BuiltInRegistries.ITEM).defaultDisk().diskHandler(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM,
            DiskItemHandler::build,
            DiskResourceItemRenderer::build
        ).driveNetworkViewHandler(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, DriveNetworkViewItemHandler::build
        )
    )

    val FLUID: DeferredHolder<ResourceType<*>, ResourceType<Fluid>> = REGISTER.registerResourceType(
        ResourceType.Builder<Fluid>(
            "fluid", DataComponents.REGISTER, Items.REGISTER
        ).registry(BuiltInRegistries.FLUID).defaultDisk().diskHandler(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM,
            DiskFluidHandler::build,
            DiskResourceFluidRenderer::build
        ).driveNetworkViewHandler(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, DriveNetworkViewFluidHandler::build
        )
    )

    fun register(event: IEventBus) {
        REGISTER.register(event)
    }

    fun addToCreativeTab(output: Output) {
        DeferredResources.REGISTRY.forEach { output.accept(it.disk) }
    }

    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        DeferredResources.REGISTRY.forEach {
            it.registerDiskCapability(event)
            event.registerItem(Capabilities.Disk.RESOURCE, { stack, _: Void? -> DiskResourceHandler(stack) }, it.disk)
            event.registerItem(
                Capabilities.Disk.MODS, { stack, _: Void? -> DiskModHandler(stack) }, it.disk
            )
            it.registerDriveNetworkViewCapability(event, BlockEntities.DRIVE_NETWORK_VIEW.get())
        }
    }
}