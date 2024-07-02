package net.asch.bulkit.common.capability.drive_network

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

class DriveNetworkViewItemHandler<C>(blockEntity: BlockEntity, ctx: C) :
    DriveNetworkViewStorage<IItemHandler>(blockEntity, Capabilities.ItemHandler.ITEM), IItemHandler {
    override fun getSlots(): Int = nSlots

    override fun getStackInSlot(slot: Int): ItemStack = execute(slot, IItemHandler::getStackInSlot, ItemStack.EMPTY)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack =
        execute(slot, IItemHandler::insertItem, stack, stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        execute(slot, IItemHandler::extractItem, ItemStack.EMPTY, amount, simulate)

    override fun getSlotLimit(slot: Int): Int = execute(slot, IItemHandler::getSlotLimit, 64)

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
        execute(slot, IItemHandler::isItemValid, false, stack)
}
