package net.asch.bulkit.common.block_entity

import com.mojang.datafixers.types.constant.EmptyPart
import net.asch.bulkit.api.BulkItApi
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object BlockEntities {
    private val register = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BulkItApi.ID)

    val DISK_DRIVE: DeferredHolder<BlockEntityType<*>, BlockEntityType<DiskDriveEntity>> =
        register.register("disk_drive") { ->
            BlockEntityType.Builder.of(::DiskDriveEntity).build(EmptyPart())
        }

    val DRIVE_NETWORK_VIEW: DeferredHolder<BlockEntityType<*>, BlockEntityType<DriveNetworkViewEntity>> =
        register.register("drive_network_view") { ->
            BlockEntityType.Builder.of(::DriveNetworkViewEntity).build(EmptyPart())
        }

    fun register(evenBus: IEventBus) = register.register(evenBus)
}