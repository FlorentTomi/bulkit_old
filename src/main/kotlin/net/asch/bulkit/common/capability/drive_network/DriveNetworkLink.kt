package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.common.block.DriveNetworkView
import net.asch.bulkit.common.data.Attachments
import net.asch.bulkit.common.data.delegate.AttachmentDelegate
import net.asch.bulkit.common.data.delegate.DefaultedAttachmentDelegate
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

class DriveNetworkLink(blockEntity: BlockEntity) {
    private val level: Level? = blockEntity.level
    private val nSlots: Int = blockEntity.blockState.getValue(DriveNetworkView.N_SLOTS_STATE)
    private val emptySlots: List<Int> = emptySlotMapping(nSlots)
    private var rootPosition: BlockPos? by AttachmentDelegate(blockEntity, Attachments.DriveNetworkView.ROOT_POS)
    private var slotMapping: MutableList<Int> by DefaultedAttachmentDelegate(
        blockEntity, Attachments.DriveNetworkView.SLOT_MAPPING, emptySlotMapping(nSlots)
    )

    fun unmap(slot: Int) = map(slot, UNMAPPED_SLOT)
    fun map(slot: Int, rootSlot: Int) {
        val mapping = slotMapping
        mapping[slot] = rootSlot
        slotMapping = mapping
    }

    fun unlink() = linkTo(null)
    fun linkTo(blockPos: BlockPos?) {
        if ((blockPos != null) && (level?.getBlockEntity(blockPos)
                ?.hasData(Attachments.DRIVE_NETWORK_DISK_STORAGE) != true)
        ) {
            return
        }

        rootPosition = blockPos
        slotMapping = emptySlots.toMutableList()
    }

    fun disk(slot: Int): ItemStack {
        val rootSlot = slotMapping[slot]
        if (rootSlot == UNMAPPED_SLOT) {
            return ItemStack.EMPTY
        }

        val rootPos = rootPosition ?: return ItemStack.EMPTY
        val rootStorage =
            level?.getBlockEntity(rootPos)?.getData(Attachments.DRIVE_NETWORK_DISK_STORAGE) ?: return ItemStack.EMPTY
        return rootStorage.getStackInSlot(rootSlot)
    }

    companion object {
        private const val UNMAPPED_SLOT: Int = -1
        private fun emptySlotMapping(size: Int): MutableList<Int> = MutableList(size) { UNMAPPED_SLOT }

        fun build(blockEntity: BlockEntity, ctx: Void) = DriveNetworkLink(blockEntity)
    }
}