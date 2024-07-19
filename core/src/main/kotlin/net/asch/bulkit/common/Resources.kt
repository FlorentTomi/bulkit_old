package net.asch.bulkit.common

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.DeferredResources
import net.asch.bulkit.api.ResourceType
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.disk.DiskFluidHandler
import net.asch.bulkit.common.capability.disk.DiskItemHandler
import net.asch.bulkit.common.capability.disk.DiskModHandler
import net.asch.bulkit.common.capability.disk.DiskResourceHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewFluidHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewItemHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.registries.DeferredHolder

object Resources {
    private val REGISTER: DeferredResources = DeferredResources(BulkIt.ID)

    val ITEM: DeferredHolder<ResourceType<*, *, *, *>, ResourceType<Item, IItemHandler, IItemHandler, Direction>> =
        REGISTER.registerResourceType(
            ResourceType.Builder<Item, IItemHandler, IItemHandler, Direction>(
                "item", DataComponents.REGISTER, Items.REGISTER
            ).registry(BuiltInRegistries.ITEM).defaultDisk()
                .diskHandler(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM) { stack, _ ->
                    DiskItemHandler(
                        stack
                    )
                }.driveNetworkViewHandler(
                    net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                    DriveNetworkViewItemHandler::build
                )
        )

    val FLUID: DeferredHolder<ResourceType<*, *, *, *>, ResourceType<Fluid, IFluidHandlerItem, IFluidHandler, Direction>> =
        REGISTER.registerResourceType(
            ResourceType.Builder<Fluid, IFluidHandlerItem, IFluidHandler, Direction>(
                "fluid", DataComponents.REGISTER, Items.REGISTER
            ).registry(BuiltInRegistries.FLUID).defaultDisk().diskHandler(
                net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM
            ) { stack, _ -> DiskFluidHandler(stack) }.driveNetworkViewHandler(
                net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, DriveNetworkViewFluidHandler::build
            )
        )

    fun register(event: IEventBus) {
        REGISTER.register(event)
    }

    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        DeferredResources.REGISTRY.forEach {
            it.registerDiskCapability(event)
            event.registerItem(Capabilities.Disk.RESOURCE, { stack, _ -> DiskResourceHandler(stack) }, it.disk)
            event.registerItem(
                Capabilities.Disk.MODS, { stack, _ -> DiskModHandler(stack) }, it.disk
            )
            it.registerDriveNetworkViewCapability(event, BlockEntities.DRIVE_NETWORK_VIEW.get())
        }
    }
}