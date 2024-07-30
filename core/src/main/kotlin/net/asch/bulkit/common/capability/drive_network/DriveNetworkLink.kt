package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.asch.bulkit.client.text.LangEntries
import net.asch.bulkit.common.data.Attachments
import net.asch.bulkit.common.data.delegate.AttachmentDelegate
import net.asch.bulkit.common.data.delegate.DefaultedAttachmentDelegate
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

class DriveNetworkLink(blockEntity: BlockEntity, @Suppress("UNUSED_PARAMETER") ctx: Direction) : IDriveNetworkLink {
    private val level: Level? = blockEntity.level
    private val nSlots: Int = blockEntity.blockState.getValue(DriveNetworkViewBase.N_SLOTS_STATE)
    private val emptySlots: List<Int> = emptySlotMapping(nSlots)
    private var rootPosition: BlockPos? by AttachmentDelegate(blockEntity, Attachments.DriveNetworkView.ROOT_POS)
    private var slotMapping: MutableList<Int> by DefaultedAttachmentDelegate(
        blockEntity, Attachments.DriveNetworkView.SLOT_MAPPING, emptySlotMapping(nSlots)
    )

    override fun map(slot: Int, rootSlot: Int) {
        val mapping = slotMapping
        mapping[slot] = rootSlot
        slotMapping = mapping
    }

    override fun linkTo(player: Player, blockPos: BlockPos?) {
        blockPos?.let {
            if (level?.getBlockEntity(it)?.hasData(Attachments.DRIVE_NETWORK_DISK_STORAGE) == true) {
                rootPosition = it
                slotMapping = emptySlots.toMutableList()

                player.displayClientMessage(LangEntries.CONFIGURATOR_LINK.component(it, rootPosition), true)
            }
        }
    }

    override fun disk(slot: Int): ItemStack {
        val rootSlot = slotMapping[slot]
        if (rootSlot == IDriveNetworkLink.UNMAPPED_SLOT) {
            return ItemStack.EMPTY
        }

        val rootPos = rootPosition ?: return ItemStack.EMPTY
        val rootStorage =
            level?.getBlockEntity(rootPos)?.getData(Attachments.DRIVE_NETWORK_DISK_STORAGE) ?: return ItemStack.EMPTY
        return rootStorage.getStackInSlot(rootSlot)
    }

    companion object {
        private fun emptySlotMapping(size: Int): MutableList<Int> =
            MutableList(size) { IDriveNetworkLink.UNMAPPED_SLOT }

        fun build(blockEntity: BlockEntity, ctx: Direction): IDriveNetworkLink =
            DriveNetworkLink(blockEntity, ctx)
    }
}