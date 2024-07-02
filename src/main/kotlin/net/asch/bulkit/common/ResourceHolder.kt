package net.asch.bulkit.common

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.block_entity.BlockEntities
import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.capability.DiskContentHandler
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

data class ResourceHolder<R, IH, BH, CH : DiskContentHandler<R>>(
    val key: String,
    val id: DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<R>>>,
    val disk: DeferredItem<Disk>,
    val diskCapability: ItemCapability<IH, Void>,
    val diskCapabilityProvider: ICapabilityProvider<ItemStack, Void, IH>,
    val diskContentCapability: ItemCapability<CH, Void>,
    val diskContentCapabilityProvider: ICapabilityProvider<ItemStack, Void, CH>,
    val blockCapability: BlockCapability<BH, Direction?>,
    val blockCapabilityProvider: ICapabilityProvider<BlockEntity, Direction?, BH>
) {
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        BulkIt.logInfo("registering $key capabilities")
        event.registerItem(diskCapability, diskCapabilityProvider, disk)
        event.registerItem(diskContentCapability, diskContentCapabilityProvider, disk)
        event.registerItem(Capabilities.DISK_MODS, { stack, _ ->
            object : ComponentItemHandler(stack, DataComponents.Disk.MODS.get(), MAX_MOD_IN_DISK) {
                override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                    return super.isItemValid(slot, stack) && (stack.item is BaseMod)
                }
            }
        }, disk)
        event.registerBlockEntity(blockCapability, BlockEntities.DRIVE_NETWORK_VIEW.get(), blockCapabilityProvider)
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
            key,
            registerResource(key, registry),
            registerDisk(key),
            diskCapability,
            diskCapabilityProvider,
            ItemCapability.createVoid(BulkIt.location("disk_content_$key"), CH::class.java),
            diskContentCapabilityProvider,
            blockCapability,
            blockCapabilityProvider
        )

        fun registerResource(
            key: String, registry: Registry<R>
        ): DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<R>>> =
            dataRegister.registerComponentType("${key}_id") {
                DataComponentType.builder<ResourceIdentifier<R>>().persistent(ResourceIdentifier.codec(registry))
                    .networkSynchronized(ResourceIdentifier.streamCodec(registry)).cacheEncoding()
            }

        fun registerDisk(key: String): DeferredItem<Disk> = itemRegister.registerItem("disk_$key") { Disk() }
    }

    companion object {
        private const val MAX_MOD_IN_DISK: Int = 4
    }
}