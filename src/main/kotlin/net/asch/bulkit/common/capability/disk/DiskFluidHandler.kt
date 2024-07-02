package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.data.resource.identifier
import net.asch.bulkit.common.data.resource.of
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class DiskFluidHandler(private val disk: ItemStack, ctx: Void) :
    DiskContentHandler<Fluid>(disk, BulkIt.RESOURCE_FLUID), IFluidHandlerItem {
    override fun getTanks(): Int = 1
    override fun getFluidInTank(tank: Int): FluidStack = toStack()
    override fun getTankCapacity(tank: Int): Int = minOf(FluidType.BUCKET_VOLUME, capacity().toInt())
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = canInsertResource(stack.identifier())
    override fun getContainer(): ItemStack = disk

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (stack.isEmpty) {
            return 0
        }

        if (!isFluidValid(0, stack)) {
            return 0
        }

        val remainingCapacity = capacity() - amount
        val amountToInsert = if (!void) minOf(remainingCapacity, stack.amount.toLong()) else stack.amount.toLong()

        if (!action.simulate()) {
            if (id == null) {
                id = stack.identifier()
            }

            amount = minOf(capacity(), amount + amountToInsert)
        }

        return amountToInsert.toInt()
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return if (id == resource.identifier()) {
            drain(resource.amount, action)
        } else {
            FluidStack.EMPTY
        }
    }

    override fun drain(amount: Int, action: IFluidHandler.FluidAction): FluidStack {
        if (amount == 0) {
            return FluidStack.EMPTY
        }

        if (id == null || this.amount == 0L) {
            return FluidStack.EMPTY
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME)
        if (this.amount <= toExtract) {
            val existing = toStack()
            if (!action.simulate() && !locked) {
                id = null
            }

            return existing
        }

        if (!action.simulate()) {
            this.amount -= toExtract
        }

        return toStack(toExtract.toLong())
    }

    private fun toStack(amount: Long): FluidStack = id?.of(amount) ?: FluidStack.EMPTY
    private fun toStack(): FluidStack = toStack(amount)

    private fun capacity(): Long = FluidType.BUCKET_VOLUME * multiplier(DEFAULT_CAPACITY_MULTIPLIER).toLong()

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32
    }
}