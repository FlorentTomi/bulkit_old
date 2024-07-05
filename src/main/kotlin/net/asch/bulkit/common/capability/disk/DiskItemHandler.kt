package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.data.resource.identifier
import net.asch.bulkit.common.data.resource.of
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class DiskItemHandler(disk: ItemStack, ctx: Void) : IItemHandler {
    private val diskContent = disk.getCapability(BulkIt.RESOURCE_ITEM.disk.contentHandler)!!

    override fun getSlots(): Int = 1
    override fun getStackInSlot(slot: Int): ItemStack = toStack()
    override fun getSlotLimit(slot: Int): Int = minOf(64, diskContent.capacity.toInt())
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean =
        DiskContentHandler.canInsertResource(diskContent.id, stack.identifier())

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }

        if (!isItemValid(slot, stack)) {
            return stack
        }

        val remainingCapacity = diskContent.capacity - diskContent.amount
        val amountToInsert =
            if (!diskContent.void) minOf(remainingCapacity, stack.count.toLong()) else stack.count.toLong()
        if (amountToInsert == 0L) {
            return stack
        }

        if (!simulate) {
            if (diskContent.id == null) {
                diskContent.id = stack.identifier()
            }

            diskContent.amount = minOf(diskContent.capacity, diskContent.amount + amountToInsert)
        }

        return if (amountToInsert == stack.count.toLong()) ItemStack.EMPTY else stack.copyWithCount(amountToInsert.toInt())
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (diskContent.amount == 0L) {
            return ItemStack.EMPTY
        }

        if (diskContent.id == null || amount == 0) {
            return ItemStack.EMPTY
        }

        val toExtract = minOf(amount, diskContent.maxStackSize)
        if (diskContent.amount <= toExtract) {
            val existing = toStack()
            if (!simulate && !diskContent.locked) {
                diskContent.id = null
            }

            return existing
        }

        if (!simulate) {
            diskContent.amount -= toExtract
        }

        return toStack(toExtract.toLong())
    }

    private fun toStack(amount: Long): ItemStack = diskContent.id?.of(amount) ?: ItemStack.EMPTY
    private fun toStack(): ItemStack = toStack(diskContent.amount)
}