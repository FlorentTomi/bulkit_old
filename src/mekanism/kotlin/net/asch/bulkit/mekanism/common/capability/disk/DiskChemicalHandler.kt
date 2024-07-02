package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.Action
import mekanism.api.chemical.Chemical
import mekanism.api.chemical.ChemicalStack
import mekanism.api.chemical.IChemicalHandler
import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.common.data.ResourceIdentifier
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidType

abstract class DiskChemicalHandler<C : Chemical<C>, S : ChemicalStack<C>>(
    disk: ItemStack, resourceHolder: ResourceHolder<C, *, *, *>
) : IChemicalHandler<C, S> {
    protected val diskContent = disk.getCapability(resourceHolder.diskContentCapability)!!

    override fun getTanks(): Int = 1
    override fun getChemicalInTank(tank: Int): S = toStack()
    override fun getTankCapacity(tank: Int): Long = minOf(FluidType.BUCKET_VOLUME.toLong(), diskContent.capacity)
    override fun isValid(tank: Int, stack: S): Boolean = diskContent.canInsertResource(toIdentifier(stack))

    override fun insertChemical(tank: Int, stack: S, action: Action): S {
        if (stack.isEmpty) {
            return emptyStack
        }

        if (!isValid(tank, stack)) {
            return stack
        }

        val remainingCapacity = diskContent.capacity - diskContent.amount
        val amountToInsert = if (!diskContent.void) minOf(remainingCapacity, stack.amount) else stack.amount
        if (amountToInsert == 0L) {
            return stack
        }

        if (!action.simulate()) {
            if (diskContent.id == null) {
                diskContent.id = toIdentifier(stack)
            }

            diskContent.amount = minOf(diskContent.capacity, diskContent.amount + amountToInsert)
        }

        @Suppress("UNCHECKED_CAST") return if (amountToInsert == stack.amount) emptyStack else (stack.copyWithAmount(
            amountToInsert
        ) as S)
    }

    override fun extractChemical(tank: Int, amount: Long, action: Action): S {
        if (diskContent.amount == 0L) {
            return emptyStack
        }

        if (diskContent.id == null || amount == 0L) {
            return emptyStack
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME.toLong())
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

        return toStack(toExtract)
    }

    override fun setChemicalInTank(tank: Int, stack: S) {
        if (diskContent.id == null) {
            diskContent.id = toIdentifier(stack)
        }
    }

    protected abstract fun toIdentifier(stack: S): ResourceIdentifier<C>
    protected abstract fun toStack(amount: Long): S
    private fun toStack(): S = toStack(diskContent.amount)
}