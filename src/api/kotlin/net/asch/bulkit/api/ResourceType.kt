package net.asch.bulkit.api

import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.api.item.Disk
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ICapabilityProvider
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

data class ResourceType<T, DH, BH, BC>(
    val key: String,
    val resource: DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<T>>>,
    val disk: DeferredItem<Disk>,
    val diskCap: ItemCapability<DH, Void>,
    private val diskCapProvider: ICapabilityProvider<ItemStack, Void, DH>,
    val driveNetworkViewCap: BlockCapability<BH, BC>,
    private val driveNetworkViewCapProvider: ICapabilityProvider<BlockEntity, BC, BH>
) {
    fun registerDiskCapability(event: RegisterCapabilitiesEvent) {
        event.registerItem(diskCap, diskCapProvider, disk)
    }

    fun <BE : BlockEntity> registerDriveNetworkViewCapability(
        event: RegisterCapabilitiesEvent, blockEntityType: BlockEntityType<BE>
    ) {
        event.registerBlockEntity(
            driveNetworkViewCap, blockEntityType, driveNetworkViewCapProvider
        )
    }

    class Builder<T, DH, BH, BC>(
        val key: String,
        private val dataComponents: DeferredRegister.DataComponents,
        private val items: DeferredRegister.Items
    ) : Supplier<ResourceType<T, DH, BH, BC>> {
        private lateinit var resource: DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<T>>>
        private lateinit var disk: DeferredItem<Disk>
        private lateinit var diskCap: ItemCapability<DH, Void>
        private lateinit var diskCapProvider: ICapabilityProvider<ItemStack, Void, DH>
        private lateinit var driveNetworkViewCap: BlockCapability<BH, BC>
        private lateinit var driveNetworkViewCapProvider: ICapabilityProvider<BlockEntity, BC, BH>

        fun registry(registry: Registry<T>): Builder<T, DH, BH, BC> {
            resource = dataComponents.registerComponentType("resource_$key") {
                DataComponentType.builder<ResourceIdentifier<T>>().persistent(ResourceIdentifier.codec(registry))
                    .networkSynchronized(ResourceIdentifier.streamCodec(registry)).cacheEncoding()
            }
            return this
        }

        fun defaultDisk(): Builder<T, DH, BH, BC> = disk { Disk() }
        fun disk(sup: (Item.Properties) -> Disk): Builder<T, DH, BH, BC> {
            disk = items.registerItem("disk_$key", sup)
            return this
        }

        fun diskHandler(
            cap: ItemCapability<DH, Void>, provider: ICapabilityProvider<ItemStack, Void, DH>
        ): Builder<T, DH, BH, BC> {
            diskCap = cap
            diskCapProvider = provider
            return this
        }

        fun driveNetworkViewHandler(
            cap: BlockCapability<BH, BC>, provider: ICapabilityProvider<BlockEntity, BC, BH>
        ): Builder<T, DH, BH, BC> {
            driveNetworkViewCap = cap
            driveNetworkViewCapProvider = provider
            return this
        }

        override fun get(): ResourceType<T, DH, BH, BC> {
            return ResourceType(
                key, resource, disk, diskCap, diskCapProvider, driveNetworkViewCap, driveNetworkViewCapProvider
            )
        }
    }
}