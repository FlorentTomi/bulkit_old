package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.data.resource.identifier
import net.asch.bulkit.common.data.resource.of
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class DiskItemHandler(disk: ItemStack, ctx: Void) : DiskContentHandler<Item>(disk, BulkIt.RESOURCE_ITEM), IItemHandler {
    override fun getSlots(): Int = 1
    override fun getStackInSlot(slot: Int): ItemStack = toStack()
    override fun getSlotLimit(slot: Int): Int = minOf(64, capacity().toInt())
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = canInsertResource(stack.identifier())

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }

        if (!isItemValid(slot, stack)) {
            return stack
        }

        val remainingCapacity = capacity() - amount
        val amountToInsert = if (!void) minOf(remainingCapacity, stack.count.toLong()) else stack.count.toLong()
        if (amountToInsert == 0L) {
            return stack
        }

        if (!simulate) {
            if (id == null) {
                id = stack.identifier()
            }

            amount = minOf(capacity(), amount + amountToInsert)
        }

        return if (amountToInsert == stack.count.toLong()) ItemStack.EMPTY else stack.copyWithCount(amountToInsert.toInt())
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (this.amount == 0L) {
            return ItemStack.EMPTY
        }

        if (id == null || amount == 0) {
            return ItemStack.EMPTY
        }

        val toExtract = minOf(amount, maxStackSize())
        if (this.amount <= toExtract) {
            val existing = toStack()
            if (!simulate && !locked) {
                id = null
            }

            return existing
        }

        if (!simulate) {
            this.amount -= toExtract
        }

        return toStack(toExtract.toLong())
    }

    private fun toStack(amount: Long): ItemStack = id?.of(amount) ?: ItemStack.EMPTY
    private fun toStack(): ItemStack = toStack(amount)

    private fun maxStackSize(): Int = id?.resource?.value()?.defaultMaxStackSize ?: 64
    private fun capacity(): Long = maxStackSize().toLong() * multiplier(DEFAULT_CAPACITY_MULTIPLIER)

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 8
    }
}