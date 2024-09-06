package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
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
        get() = disk.get(resourceType.id)
        set(value) {
            disk.set(resourceType.id, value)
            resource.amountL = 0L
        }

    private val capacity: Int
        get() = capacity(resource)

    override fun getTanks(): Int = 1
    override fun getFluidInTank(tank: Int): FluidStack = toStack()
    override fun getTankCapacity(tank: Int): Int = minOf(FluidType.BUCKET_VOLUME, capacity)
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = (id == null) || (id == stack.identifier())

    override fun getContainer(): ItemStack = disk

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        if (stack.isEmpty) {
            return 0
        }

        if (!isFluidValid(0, stack)) {
            return 0
        }

        val remainingCapacity = capacity - resource.amountI
        val amountToInsert =
            if (!resource.isVoidExcess) minOf(remainingCapacity, stack.amount) else stack.amount

        if (action.execute()) {
            if (id == null) {
                id = stack.identifier()
            }

            resource.amountI = minOf(capacity, resource.amountI + amountToInsert)
        }

        return amountToInsert
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

        if (id == null || resource.amountI == 0) {
            return FluidStack.EMPTY
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME)
        if (resource.amountI <= toExtract) {
            val existing = toStack()
            if (action.execute()) {
                if (!resource.isLocked) {
                    id = null
                } else {
                    resource.amountI = 0
                }
            }

            return existing
        }

        if (action.execute()) {
            resource.amountI -= toExtract
        }

        return toStack(toExtract)
    }

    private fun toStack(amount: Int): FluidStack = id?.of(amount) ?: FluidStack.EMPTY
    private fun toStack(): FluidStack = toStack(resource.amountI)

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32

        @Suppress("UNUSED_PARAMETER")
        fun build(stack: ItemStack, ctx: Void?) = DiskFluidHandler(stack)

        fun capacity(resource: IDiskResourceHandler): Int =
            (FluidType.BUCKET_VOLUME * resource.getMultiplier(DEFAULT_CAPACITY_MULTIPLIER))
    }
}