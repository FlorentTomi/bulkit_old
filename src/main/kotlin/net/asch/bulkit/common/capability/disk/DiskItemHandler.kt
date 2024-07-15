package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkItCore
import net.asch.bulkit.api.capability.BulkItCapabilities
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.data.extensions.identifier
import net.asch.bulkit.common.data.extensions.of
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class DiskItemHandler(private val disk: ItemStack) : IItemHandler {
    private val resourceType = Resources.ITEM.get()
    private val resource = disk.getCapability(BulkItCapabilities.Disk.RESOURCE)!!
    private var id: ResourceIdentifier<Item>?
        get() = disk.get(resourceType.resource)
        set(value) {
            disk.set(resourceType.resource, value)
            resource.amount = 0
        }

    private val maxStackSize: Int
        get() = toStack().getOrDefault(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, 1)
    private val capacity: Long
        get() = maxStackSize.toLong() * resource.multiplier(DEFAULT_CAPACITY_MULTIPLIER)

    override fun getSlots(): Int = 1
    override fun getStackInSlot(slot: Int): ItemStack = toStack()
    override fun getSlotLimit(slot: Int): Int = capacity.toInt()
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = (id == null) || (id == stack.identifier())

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.isEmpty) {
            return ItemStack.EMPTY
        }

        if (!isItemValid(slot, stack)) {
            return stack
        }

        val remainingCapacity = capacity - resource.amount
        val amountToInsert =
            if (!resource.void) minOf(remainingCapacity, stack.count.toLong()) else stack.count.toLong()
        if (amountToInsert == 0L) {
            return stack
        }

        if (!simulate) {
            if (id == null) {
                id = stack.identifier()
            }

            resource.amount = minOf(capacity, resource.amount + amountToInsert)
        }

        return if (amountToInsert == stack.count.toLong()) ItemStack.EMPTY else stack.copyWithCount(amountToInsert.toInt())
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        if (resource.amount == 0L) {
            return ItemStack.EMPTY
        }

        if (id == null || amount == 0) {
            return ItemStack.EMPTY
        }

        val toExtract = minOf(amount, maxStackSize)
        if (resource.amount <= toExtract) {
            val existing = toStack()
            if (!simulate && !resource.locked) {
                id = null
            }

            return existing
        }

        if (!simulate) {
            resource.amount -= toExtract
        }

        return toStack(toExtract.toLong())
    }

    private fun toStack(amount: Long): ItemStack = id?.of(amount) ?: ItemStack.EMPTY
    private fun toStack(): ItemStack = toStack(resource.amount)

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 8

        fun build(stack: ItemStack, ctx: Void) = DiskItemHandler(stack)
    }
}