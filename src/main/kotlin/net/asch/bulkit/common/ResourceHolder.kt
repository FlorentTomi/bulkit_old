package net.asch.bulkit.common

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.capability.disk.DiskContentHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.data.ResourceIdentifier
import net.asch.bulkit.common.item.BaseMod
import net.asch.bulkit.common.item.Disk
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.ComponentItemHandler
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

data class ResourceHolder<R, IH, BH>(
    val key: String,
    val id: DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<R>>>,
    val disk: Disk<R, IH>,
    val driveNetworkView: DriveNetworkView<BH>
) {
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        BulkIt.logInfo("registering $key capabilities")
        disk.register(event)
        driveNetworkView.register(event)
    }

    data class Disk<R, IH>(
        val disk: DeferredItem<net.asch.bulkit.common.item.Disk>,
        val resourceHandler: ItemCapability<IH, Void>,
        private val resourceHandlerProvider: ICapabilityProvider<ItemStack, Void, IH>,
        private val contentHandlerProvider: ICapabilityProvider<ItemStack, Void, DiskContentHandler>
    ) {
        fun register(event: RegisterCapabilitiesEvent) {
            event.registerItem(resourceHandler, resourceHandlerProvider, disk)
            event.registerItem(
                Capabilities.DISK_CONTENT, { stack, ctx -> contentHandlerProvider.getCapability(stack, ctx) }, disk
            )
            event.registerItem(Capabilities.DISK_MODS, { stack, _ ->
                object : ComponentItemHandler(stack, DataComponents.Disk.MODS.get(), MAX_MOD_IN_DISK) {
                    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                        return super.isItemValid(slot, stack) && (stack.item is BaseMod)
                    }
                }
            }, disk)
        }
    }

    data class DriveNetworkView<BH>(
        val resourceHandler: BlockCapability<BH, Direction?>,
        private val resourceHandlerProvider: ICapabilityProvider<BlockEntity, Direction?, BH>
    ) {
        fun register(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(resourceHandler, BlockEntities.DRIVE_NETWORK_VIEW.get(), resourceHandlerProvider)
        }
    }

    data class Builder<R>(val dataRegister: DeferredRegister.DataComponents, val itemRegister: DeferredRegister.Items) {
        inline fun <IH, BH, reified CH : DiskContentHandler<R>> build(
            key: String,
            registry: Registry<R>,
            diskCapability: ItemCapability<IH, Void>,
            diskCapabilityProvider: ICapabilityProvider<ItemStack, Void, IH>,
            diskContentCapabilityProvider: ICapabilityProvider<ItemStack, Void, CH>,
            blockCapability: BlockCapability<BH, Direction?>,
            blockCapabilityProvider: ICapabilityProvider<BlockEntity, Direction?, BH>
        ): ResourceHolder<R, IH, BH, CH> = ResourceHolder(
            key, registerResource(key, registry), Disk(
                registerDisk(key), diskCapability, ItemCapability.createVoid(
                    BulkIt.location("disk_content_$key"), CH::class.java
                ), diskCapabilityProvider, diskContentCapabilityProvider
            ), DriveNetworkView(blockCapability, blockCapabilityProvider)
        )

        fun registerResource(
            key: String, registry: Registry<R>
        ): DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<R>>> =
            dataRegister.registerComponentType("${key}_id") {
                DataComponentType.builder<ResourceIdentifier<R>>().persistent(ResourceIdentifier.codec(registry))
                    .networkSynchronized(ResourceIdentifier.streamCodec(registry)).cacheEncoding()
            }

        fun registerDisk(key: String): DeferredItem<net.asch.bulkit.common.item.Disk> =
            itemRegister.registerItem("disk_$key") { Disk() }
    }

    companion object {
        private const val MAX_MOD_IN_DISK: Int = 4

    }
}