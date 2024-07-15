package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.BulkItCore
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.block.DriveNetworkView
import net.asch.bulkit.common.capability.Capabilities
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.IItemHandler

class DriveNetworkViewItemHandler(blockEntity: BlockEntity) : IItemHandler {
    private val resourceType = Resources.ITEM.get()
    private val nSlots = blockEntity.blockState.getValue(DriveNetworkView.N_SLOTS_STATE)
    private val link: DriveNetworkLink? = blockEntity.level?.getCapability(
        Capabilities.DRIVE_NETWORK_LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, null
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