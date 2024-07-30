package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.asch.bulkit.common.Resources
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class DriveNetworkViewFluidHandler(blockEntity: BlockEntity, ctx: Direction) : IFluidHandler {
    private val resourceType = Resources.FLUID.get()
    private val nSlots = blockEntity.blockState.getValue(DriveNetworkViewBase.N_SLOTS_STATE)
    private val link: IDriveNetworkLink? = blockEntity.level?.getCapability(
        Capabilities.DriveNetwork.LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, ctx
    )

    override fun getTanks(): Int = nSlots

    override fun getFluidInTank(tank: Int): FluidStack =
        link?.disk(tank)?.getCapability(CAP)?.getFluidInTank(0) ?: FluidStack.EMPTY

    override fun getTankCapacity(tank: Int): Int = link?.disk(tank)?.getCapability(CAP)?.getTankCapacity(0) ?: 0

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean =
        link?.disk(tank)?.getCapability(CAP)?.isFluidValid(0, stack) ?: false

    override fun fill(stack: FluidStack, action: IFluidHandler.FluidAction): Int {
        val remainingStack = stack.copy()
        for (tank in 0 until nSlots) {
            val filledAmount = link?.disk(tank)?.getCapability(CAP)?.fill(remainingStack, action) ?: 0
            remainingStack.shrink(filledAmount)
        }

        return stack.amount - remainingStack.amount
    }

    override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
        val resourceToDrain = resource.copy()
        var drainedStack = FluidStack.EMPTY
        for (tank in 0 until nSlots) {
            if (resourceToDrain.isEmpty) {
                break
            }

            val drained = link?.disk(tank)?.getCapability(CAP)?.drain(resourceToDrain, action) ?: FluidStack.EMPTY
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
        for (tank in 0 until nSlots) {
            if (drainedStack.amount == amount) {
                break
            }

            if (drainedStack.isEmpty) {
                drainedStack = link?.disk(tank)?.getCapability(CAP)?.drain(amount, action) ?: FluidStack.EMPTY
            } else {
                val toDrain = drainedStack.copyWithAmount(amount - drainedStack.amount)
                val drained = link?.disk(tank)?.getCapability(CAP)?.drain(toDrain, action) ?: FluidStack.EMPTY
                if (!drained.isEmpty) {
                    drainedStack.grow(drained.amount)
                }
            }
        }

        return drainedStack
    }

    companion object {
        private val CAP = net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM

        fun build(blockEntity: BlockEntity, ctx: Direction) = DriveNetworkViewFluidHandler(blockEntity, ctx)
    }
}