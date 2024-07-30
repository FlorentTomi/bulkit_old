package net.asch.bulkit.common.data

import com.mojang.serialization.Codec
import net.asch.bulkit.api.BulkIt
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object DataComponents {
    val REGISTER: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(BulkIt.ID)

    val DISK_AMOUNT: DeferredHolder<DataComponentType<*>, DataComponentType<Long>> =
        REGISTER.registerComponentType("disk_amount") {
            DataComponentType.builder<Long>().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
        }

    val DISK_LOCKED: DeferredHolder<DataComponentType<*>, DataComponentType<Boolean>> =
        REGISTER.registerComponentType("disk_locked") {
            DataComponentType.builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
        }

    val DISK_VOID: DeferredHolder<DataComponentType<*>, DataComponentType<Boolean>> =
        REGISTER.registerComponentType("disk_void") {
            DataComponentType.builder<Boolean>().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
        }

    val DISK_MODS: DeferredHolder<DataComponentType<*>, DataComponentType<ItemContainerContents>> =
        REGISTER.registerComponentType("disk_mods") {
            DataComponentType.builder<ItemContainerContents>().persistent(ItemContainerContents.CODEC)
                .networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding()
        }

    val CONFIGURATOR_ROOT_POS: DeferredHolder<DataComponentType<*>, DataComponentType<BlockPos>> =
        REGISTER.registerComponentType("configurator_root_pos") {
            DataComponentType.builder<BlockPos>().persistent(BlockPos.CODEC).cacheEncoding()
        }

    fun register(eventBus: IEventBus) = REGISTER.register(eventBus)
}