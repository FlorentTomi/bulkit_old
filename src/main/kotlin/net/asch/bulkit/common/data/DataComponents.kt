package net.asch.bulkit.common.data

import com.mojang.serialization.Codec
import net.asch.bulkit.BulkIt
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object DataComponents {
    val REGISTER: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(BulkIt.ID)

    object Disk {
        val AMOUNT: DeferredHolder<DataComponentType<*>, DataComponentType<Long>> =
            REGISTER.registerComponentType("disk_amount") {
                DataComponentType.builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
            }

        val LOCKED: DeferredHolder<DataComponentType<*>, DataComponentType<Boolean>> =
            REGISTER.registerComponentType("disk_locked") {
                DataComponentType.builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
            }

        val VOID: DeferredHolder<DataComponentType<*>, DataComponentType<Boolean>> =
            REGISTER.registerComponentType("disk_void") {
                DataComponentType.builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
            }

        val MODS: DeferredHolder<DataComponentType<*>, DataComponentType<ItemContainerContents>> =
            REGISTER.registerComponentType("disk_mods") {
                DataComponentType.builder<ItemContainerContents>().persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding()
            }
    }

    fun register(eventBus: IEventBus) = REGISTER.register(eventBus)
    fun <R> registerResource(
        key: String, resourceRegistry: Registry<R>
    ): DeferredHolder<DataComponentType<*>, DataComponentType<ResourceIdentifier<R>>> =
        REGISTER.registerComponentType("${key}_id") {
            BulkIt.logInfo("Registering ${resourceRegistry.key().registry()} resource")
            DataComponentType.builder<ResourceIdentifier<R>>().persistent(ResourceIdentifier.codec(resourceRegistry))
                .networkSynchronized(ResourceIdentifier.streamCodec(resourceRegistry)).cacheEncoding()
        }
}