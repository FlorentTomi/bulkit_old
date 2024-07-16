package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.api.block.BulkItBlockStates
import net.asch.bulkit.api.capability.BulkItCapabilities
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.asch.bulkit.common.Resources
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.IItemHandler

class DriveNetworkViewItemHandler(blockEntity: BlockEntity) : IItemHandler {
    private val resourceType = Resources.ITEM.get()
    private val nSlots = blockEntity.blockState.getValue(BulkItBlockStates.DriveNetworkView.N_SLOTS_STATE)
    private val link: IDriveNetworkLink? = blockEntity.level?.getCapability(
        BulkItCapabilities.DriveNetwork.LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, null
    )

    override fun getSlots(): Int = nSlots

    override fun getStackInSlot(slot: Int): ItemStack =
        link?.disk(slot)?.getCapability(resourceType.diskCap)?.getStackInSlot(0) ?: ItemStack.EMPTY

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        link?.disk(slot)?.getCapability(resourceType.diskCap)?.insertItem(0, stack, simulate) ?: stack

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        link?.disk(slot)?.getCapability(resourceType.diskCap)?.extractItem(0, amount, simulate) ?: ItemStack.EMPTY

    override fun getSlotLimit(slot: Int): Int =
        link?.disk(slot)?.getCapability(resourceType.diskCap)?.getSlotLimit(0) ?: 0

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
        link?.disk(slot)?.getCapability(resourceType.diskCap)?.isItemValid(0, stack) ?: false

    companion object {
        fun build(blockEntity: BlockEntity, ctx: Direction?) = DriveNetworkViewItemHandler(blockEntity)
    }
}