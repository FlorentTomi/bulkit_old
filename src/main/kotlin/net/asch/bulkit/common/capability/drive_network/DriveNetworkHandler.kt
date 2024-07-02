package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.common.capability.Capabilities
import net.asch.bulkit.common.data.Attachments
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.items.IItemHandler
import kotlin.jvm.optionals.getOrNull

class DriveNetworkHandler(private val blockEntity: BlockEntity, ctx: Void) {
    fun setRoot(blockPos: BlockPos) {
        blockEntity.setData(Attachments.DRIVE_NETWORK_ROOT_POS, blockPos)
    }

    fun clearRoot() {
        blockEntity.removeData(Attachments.DRIVE_NETWORK_ROOT_POS)
    }

    fun rootStorage(): IItemHandler? =
        blockEntity.getExistingData(Attachments.DRIVE_NETWORK_ROOT_POS).getOrNull()?.let {
            blockEntity.level?.getCapability(Capabilities.DISK_STORAGE, it, null)
        }

    fun map(viewSlot: Int, rootSlot: Int) {
        val slotMapping = blockEntity.getData(Attachments.DRIVE_NETWORK_VIEW_SLOT_MAP).toMutableList()
        slotMapping[viewSlot] = rootSlot
        blockEntity.setData(Attachments.DRIVE_NETWORK_VIEW_SLOT_MAP, slotMapping)
    }

    fun clear(viewSlot: Int) = map(viewSlot, -1)

    fun rootSlot(viewSlot: Int): Int? {
        val rootSlot = blockEntity.getData(Attachments.DRIVE_NETWORK_VIEW_SLOT_MAP)[viewSlot]
        return if (rootSlot == -1) null else rootSlot
    }

    fun <H> forEach(cap: ItemCapability<H, Void>, fn: (H) -> Unit) {
        blockEntity.getData(Attachments.DRIVE_NETWORK_VIEW_SLOT_MAP).filter { it != -1 }.mapNotNull {
            rootStorage()?.getStackInSlot(it)?.getCapability(cap)
        }
    }
}