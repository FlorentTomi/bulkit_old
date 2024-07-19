package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.data.extensions.identifier
import net.asch.bulkit.common.data.extensions.of
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class DiskFluidHandler(private val disk: ItemStack) : IFluidHandlerItem {
    private val resourceType = Resources.FLUID.get()
    private val resource = disk.getCapability(Capabilities.Disk.RESOURCE)!!
    private var id: ResourceIdentifier<Fluid>?
        get() = disk.get(resourceType.resource)
        set(value) {
            disk.set(resourceType.resource, value)
            resource.amount = 0;
        }

    private val maxStackSize: Int
        get() = toStack().getOrDefault(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, 1)
    private val capacity: Long
        get() = maxStackSize.toLong() * resource.multiplier(DEFAULT_CAPACITY_MULTIPLIER)

    override fun getTanks(): Int = 1
    override fun getFluidInTank(tank: Int): FluidStack = toStack()
    override fun getTankCapacity(tank: Int): Int = minOf(FluidType.BUCKET_VOLUME, capacity.toInt())
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = (id == null) || (id == stack.identifier())

    override fun getContainer(): ItemStack = disk

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (stack.isEmpty) {
            return 0
        }

        if (!isFluidValid(0, stack)) {
            return 0
        }

        val remainingCapacity = capacity - resource.amount
        val amountToInsert =
            if (!resource.isVoidExcess) minOf(remainingCapacity, stack.amount.toLong()) else stack.amount.toLong()

        if (!action.simulate()) {
            if (id == null) {
                id = stack.identifier()
            }

            resource.amount = minOf(capacity, resource.amount + amountToInsert)
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

        if (id == null || resource.amount == 0L) {
            return FluidStack.EMPTY
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME)
        if (resource.amount <= toExtract) {
            val existing = toStack()
            if (!action.simulate() && !resource.isLocked) {
                id = null
            }

            return existing
        }

        if (!action.simulate()) {
            resource.amount -= toExtract
        }

        return toStack(toExtract.toLong())
    }

    private fun toStack(amount: Long): FluidStack = id?.of(amount) ?: FluidStack.EMPTY
    private fun toStack(): FluidStack = toStack(resource.amount)

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32
    }
}