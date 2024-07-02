package net.asch.bulkit.common.data

import com.mojang.serialization.Codec
import net.asch.bulkit.BulkIt
import net.minecraft.core.BlockPos
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.items.ItemStackHandler
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object Attachments {
    private val register = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BulkIt.ID)

    val DRIVE_NETWORK_DISK_STORAGE: DeferredHolder<AttachmentType<*>, AttachmentType<ItemStackHandler>> =
        register.register("drive_network_disk_storage") { ->
            AttachmentType.serializable { -> ItemStackHandler(DISK_STORAGE_SIZE) }.build()
        }

    val DRIVE_NETWORK_ROOT_POS: DeferredHolder<AttachmentType<*>, AttachmentType<BlockPos>> =
        register.register("drive_network_root_pos") { ->
            AttachmentType.builder { -> BlockPos(0, 0, 0) }.serialize(BlockPos.CODEC).build()
        }

    val DRIVE_NETWORK_VIEW_SLOT_MAP: DeferredHolder<AttachmentType<*>, AttachmentType<List<Int>>> =
        register.register("drive_network_view_map") { ->
            AttachmentType.builder { -> listOf<Int>() }.serialize(Codec.INT.listOf()).build()
        }

    fun register(eventBus: IEventBus) = register.register(eventBus)

    private const val DISK_STORAGE_SIZE: Int = 8
}