package net.asch.bulkit.common.data

import com.mojang.serialization.Codec
import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.item.Disk
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
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
            AttachmentType.serializable { -> DiskHandler() as ItemStackHandler }.build()
        }

    fun register(eventBus: IEventBus) = register.register(eventBus)

    object DriveNetworkView {
        val ROOT_POS: DeferredHolder<AttachmentType<*>, AttachmentType<BlockPos>> =
            register.register("drive_network_view_root_pos") { ->
                AttachmentType.builder { -> BlockPos.ZERO }.serialize(BlockPos.CODEC).build()
            }

        val SLOT_MAPPING: DeferredHolder<AttachmentType<*>, AttachmentType<MutableList<Int>>> =
            register.register("drive_network_view_root_slot_mapping") { ->
                AttachmentType.builder { -> emptyList<Int>().toMutableList() }.serialize(Codec.INT.listOf()).build()
            }
    }

    private class DiskHandler : ItemStackHandler(DISK_STORAGE_SIZE) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return super.isItemValid(slot, stack) && (stack.item is Disk)
        }
    }

    private const val DISK_STORAGE_SIZE: Int = 8
}