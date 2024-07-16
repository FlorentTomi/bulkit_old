package net.asch.bulkit.common

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.DeferredResources
import net.asch.bulkit.api.ResourceType
import net.asch.bulkit.api.capability.BulkItCapabilities
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

object Resources {
    private val REGISTER: DeferredResources = DeferredResources(BulkIt.ID)

    val ITEM = REGISTER.registerResourceType(
        ResourceType.Builder<Item, IItemHandler, IItemHandler, Direction?>(
            "item", DataComponents.REGISTER, Items.REGISTER
        ).registry(BuiltInRegistries.ITEM).defaultDisk()
            .diskHandler(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM, DiskItemHandler::build)
            .driveNetworkViewHandler(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, DriveNetworkViewItemHandler::build
            )
    )

    val FLUID = REGISTER.registerResourceType(
        ResourceType.Builder<Fluid, IFluidHandlerItem, IFluidHandler, Direction?>(
            "fluid", DataComponents.REGISTER, Items.REGISTER
        ).registry(BuiltInRegistries.FLUID).defaultDisk().diskHandler(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM, DiskFluidHandler::build
        ).driveNetworkViewHandler(
            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, DriveNetworkViewFluidHandler::build
        )
    )

    fun register(event: IEventBus) {
        REGISTER.register(event)
    }

    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        DeferredResources.REGISTRY.forEach {
            BulkIt.logDebug("registering ${it.key} capabilities")
            it.registerDiskCapability(event)
            event.registerItem(BulkItCapabilities.Disk.RESOURCE, DiskResourceHandler::build, it.disk)
            event.registerItem(
                BulkItCapabilities.Disk.MODS, ::DiskModHandler, it.disk
            )
            it.registerDriveNetworkViewCapability(event, BlockEntities.DRIVE_NETWORK_VIEW.get())
        }

//        REGISTER.entries.forEach { resourceTypeHolder ->
//            val resourceType = resourceTypeHolder.get()
//            resourceType.registerDiskCapability(event)
//            event.registerItem(BulkItCapabilities.Disk.RESOURCE, DiskResourceHandler::build, resourceType.disk)
//            event.registerItem(
//                BulkItCapabilities.Disk.MODS, ::DiskModHandler, resourceType.disk
//            )
//            resourceType.registerDriveNetworkViewCapability(event, BlockEntities.DRIVE_NETWORK_VIEW.get())
//        }
    }
}