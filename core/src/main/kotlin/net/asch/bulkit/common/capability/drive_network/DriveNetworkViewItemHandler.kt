package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.asch.bulkit.common.Resources
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.IItemHandler

class DriveNetworkViewItemHandler(blockEntity: BlockEntity, ctx: Direction) : IItemHandler {
    private val resourceType = Resources.ITEM.get()
    private val nSlots = blockEntity.blockState.getValue(DriveNetworkViewBase.N_SLOTS_STATE)
    private val link: IDriveNetworkLink? = blockEntity.level?.getCapability(
        Capabilities.DriveNetwork.LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, ctx
    )

    override fun getSlots(): Int = nSlots

    override fun getStackInSlot(slot: Int): ItemStack =
        link?.disk(slot)?.getCapability(CAP)?.getStackInSlot(0) ?: ItemStack.EMPTY

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        link?.disk(slot)?.getCapability(CAP)?.insertItem(0, stack, simulate) ?: stack

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        link?.disk(slot)?.getCapability(CAP)?.extractItem(0, amount, simulate) ?: ItemStack.EMPTY

    override fun getSlotLimit(slot: Int): Int = link?.disk(slot)?.getCapability(CAP)?.getSlotLimit(0) ?: 0

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
        link?.disk(slot)?.getCapability(CAP)?.isItemValid(0, stack) ?: false

    companion object {
        private val CAP = net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM

        fun build(blockEntity: BlockEntity, ctx: Direction) = DriveNetworkViewItemHandler(blockEntity, ctx)
    }
}