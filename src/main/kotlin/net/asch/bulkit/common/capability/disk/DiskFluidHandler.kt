package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.data.resource.identifier
import net.asch.bulkit.common.data.resource.of
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class DiskFluidHandler(private val disk: ItemStack, ctx: Void) : IFluidHandlerItem {
    private val diskContent = disk.getCapability(BulkIt.RESOURCE_FLUID.disk.contentHandler)!!

    override fun getTanks(): Int = 1
    override fun getFluidInTank(tank: Int): FluidStack = toStack()
    override fun getTankCapacity(tank: Int): Int = minOf(FluidType.BUCKET_VOLUME, capacity().toInt())
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = diskContent.canInsertResource(stack.identifier())
    override fun getContainer(): ItemStack = disk

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (stack.isEmpty) {
            return 0
        }

        if (!isFluidValid(0, stack)) {
            return 0
        }

        val remainingCapacity = capacity() - diskContent.amount
        val amountToInsert = if (!diskContent.void) minOf(remainingCapacity, stack.amount.toLong()) else stack.amount.toLong()

        if (!action.simulate()) {
            if (diskContent.id == null) {
                diskContent.id = stack.identifier()
            }

            diskContent.amount = minOf(capacity(), diskContent.amount + amountToInsert)
        }

        return amountToInsert.toInt()
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        return if (diskContent.id == resource.identifier()) {
            drain(resource.amount, action)
        } else {
            FluidStack.EMPTY
        }
    }

    override fun drain(amount: Int, action: IFluidHandler.FluidAction): FluidStack {
        if (amount == 0) {
            return FluidStack.EMPTY
        }

        if (diskContent.id == null || diskContent.amount == 0L) {
            return FluidStack.EMPTY
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME)
        if (diskContent.amount <= toExtract) {
            val existing = toStack()
            if (!action.simulate() && !diskContent.locked) {
                diskContent.id = null
            }

            return existing
        }

        if (!action.simulate()) {
            diskContent.amount -= toExtract
        }

        return toStack(toExtract.toLong())
    }

    private fun toStack(amount: Long): FluidStack = diskContent.id?.of(amount) ?: FluidStack.EMPTY
    private fun toStack(): FluidStack = toStack(diskContent.amount)

    private fun capacity(): Long = FluidType.BUCKET_VOLUME * diskContent.multiplier(DEFAULT_CAPACITY_MULTIPLIER).toLong()

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32
    }
}