package net.asch.bulkit.api.registry

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.api.item.Disk
import net.asch.bulkit.api.registry.ResourceType.IDiskCapabilityRegister
import net.asch.bulkit.api.registry.ResourceType.IDriveNetworkViewCapabilityRegister
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
import java.util.function.Function
import java.util.function.Supplier

class ResourceType<T>(
    val key: String,
    val id: DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<T>>>,
    val disk: DeferredItem<Disk>,
    private val diskCapRegister: IDiskCapabilityRegister,
    private val driveNetworkViewCapRegister: IDriveNetworkViewCapabilityRegister
) {
    fun registerDiskCapability(event: RegisterCapabilitiesEvent) = diskCapRegister.register(event)

    fun <BE : BlockEntity> registerDriveNetworkViewCapability(
        event: RegisterCapabilitiesEvent, blockEntityType: BlockEntityType<BE>
    ) = driveNetworkViewCapRegister.register(event, blockEntityType)

    class Builder<T>(
        val key: String, val dataComponents: DeferredRegister.DataComponents, private val items: DeferredRegister.Items
    ) : Supplier<ResourceType<T>> {
        lateinit var id: DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<T>>>
        private lateinit var disk: DeferredItem<Disk>
        private lateinit var diskCapRegister: IDiskCapabilityRegister
        private lateinit var driveNetworkViewCapRegister: IDriveNetworkViewCapabilityRegister

        fun registry(registry: Registry<T>): Builder<T> {
            id = dataComponents.registerComponentType("resource_$key") { builder ->
                builder.persistent(ResourceIdentifier.codec(registry))
                    .networkSynchronized(ResourceIdentifier.streamCodec(registry)).cacheEncoding()
            }

            return this
        }

        fun defaultDisk(): Builder<T> = disk { properties -> Disk(properties) }
        fun disk(sup: Function<Item.Properties, Disk>): Builder<T> {
            disk = items.registerItem("disk_$key", sup)
            return this
        }

        fun <H> diskHandler(
            cap: ItemCapability<H, Void?>,
            resourceProvider: ICapabilityProvider<ItemStack, Void?, H>
        ): Builder<T> {
            diskCapRegister = IDiskCapabilityRegister { event ->
                event.registerItem(cap, resourceProvider, disk)
            }
            return this
        }

        fun <H, C> driveNetworkViewHandler(
            cap: BlockCapability<H, C>, provider: ICapabilityProvider<BlockEntity, C, H>
        ): Builder<T> {
            driveNetworkViewCapRegister = IDriveNetworkViewCapabilityRegister { event, blockEntityType ->
                event.registerBlockEntity(
                    cap, blockEntityType, provider
                )
            }
            return this
        }

        override fun get(): ResourceType<T> {
            return ResourceType(key, id, disk, diskCapRegister, driveNetworkViewCapRegister)
        }
    }


    fun interface IDiskCapabilityRegister {
        fun register(event: RegisterCapabilitiesEvent)
    }

    fun interface IDriveNetworkViewCapabilityRegister {
        fun register(event: RegisterCapabilitiesEvent, blockEntityType: BlockEntityType<out BlockEntity>)
    }
}

fun ResourceType.Builder<Unit>.resourceLess(): ResourceType.Builder<Unit> {
    id =
        dataComponents.registerComponentType("resource_$key") { builder -> builder.persistent(ResourceIdentifier.RESOURCELESS_CODEC) }
    return this
}