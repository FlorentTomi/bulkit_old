package net.asch.bulkit.common.capability.drive_network

import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem

class DriveNetworkViewFluidHandler<C>(blockEntity: BlockEntity, ctx: C) :
    DriveNetworkViewStorage<IFluidHandlerItem>(blockEntity, Capabilities.FluidHandler.ITEM), IFluidHandler {
    override fun getTanks(): Int = nSlots

    override fun getFluidInTank(tank: Int): FluidStack =
        execute(tank, IFluidHandlerItem::getFluidInTank, FluidStack.EMPTY)

    override fun getTankCapacity(tank: Int): Int =
        execute(tank, IFluidHandlerItem::getTankCapacity, FluidType.BUCKET_VOLUME)

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean =
        execute(tank, IFluidHandlerItem::isFluidValid, false, stack)

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        val remainingStack = stack.copy()
        forEach<IFluidHandlerItem>(Capabilities.FluidHandler.ITEM) {
            val filledAmount = it.fill(remainingStack, action)
            remainingStack.shrink(filledAmount)
        }

        return stack.amount - remainingStack.amount
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        val resourceToDrain = resource.copy()
        var drainedStack = FluidStack.EMPTY
        forEach<IFluidHandlerItem>(Capabilities.FluidHandler.ITEM) {
            if (resourceToDrain.isEmpty) {
                return@forEach
            }

            val drained = it.drain(resourceToDrain, action)
            if (drainedStack.isEmpty) {
                drainedStack = drained.copy()
            } else {
                drainedStack.grow(drained.amount)
            }
            resourceToDrain.shrink(drained.amount)
        }

        return drainedStack
    }

    override fun drain(amount: Int, action: IFluidHandler.FluidAction): FluidStack {
        var drainedStack = FluidStack.EMPTY
        forEach<IFluidHandlerItem>(Capabilities.FluidHandler.ITEM) {
            if (drainedStack.amount == amount) {
                return@forEach
            }

            if (drainedStack.isEmpty) {
                drainedStack = it.drain(amount, action)
            } else {
                val toDrain = drainedStack.copyWithAmount(amount - drainedStack.amount)
                val drained = it.drain(toDrain, action)
                if (!drained.isEmpty) {
                    drainedStack.grow(drained.amount)
                }
            }
        }

        return drainedStack
    }
}