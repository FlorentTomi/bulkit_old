package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.Action
import mekanism.api.chemical.Chemical
import mekanism.api.chemical.ChemicalStack
import mekanism.api.chemical.IChemicalHandler
import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.common.capability.disk.DiskContentHandler
import net.asch.bulkit.common.data.ResourceIdentifier
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidType

abstract class DiskChemicalHandler<C : Chemical<C>, S : ChemicalStack<C>>(
    disk: ItemStack, resourceHolder: ResourceHolder<C, *, *>
) : DiskContentHandler<C>(disk, resourceHolder), IChemicalHandler<C, S> {
    override fun getTanks(): Int = 1
    override fun getChemicalInTank(tank: Int): S = toStack()
    override fun getTankCapacity(tank: Int): Long = minOf(FluidType.BUCKET_VOLUME.toLong(), capacity())
    override fun isValid(tank: Int, stack: S): Boolean = canInsertResource(toIdentifier(stack))

    override fun insertChemical(tank: Int, stack: S, action: Action): S {
        if (stack.isEmpty) {
            return emptyStack
        }

        if (!isValid(tank, stack)) {
            return stack
        }

        val remainingCapacity = capacity() - amount
        val amountToInsert = if (!void) minOf(remainingCapacity, stack.amount) else stack.amount
        if (amountToInsert == 0L) {
            return stack
        }

        if (!action.simulate()) {
            if (id == null) {
                id = toIdentifier(stack)
            }

            amount = minOf(capacity(), amount + amountToInsert)
        }

        @Suppress("UNCHECKED_CAST") return if (amountToInsert == stack.amount) emptyStack else (stack.copyWithAmount(
            amountToInsert
        ) as S)
    }

    override fun extractChemical(tank: Int, amount: Long, action: Action): S {
        if (this.amount == 0L) {
            return emptyStack
        }

        if (id == null || amount == 0L) {
            return emptyStack
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME.toLong())
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

        return toStack(toExtract)
    }

    override fun setChemicalInTank(tank: Int, stack: S) {
        if (id == null) {
            id = toIdentifier(stack)
        }
    }

    protected abstract fun toIdentifier(stack: S): ResourceIdentifier<C>
    protected abstract fun toStack(amount: Long): S
    private fun toStack(): S = toStack(amount)

    private fun capacity(): Long = FluidType.BUCKET_VOLUME * multiplier(DEFAULT_CAPACITY_MULTIPLIER).toLong()

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32
    }
}