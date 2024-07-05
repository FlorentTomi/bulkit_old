package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.Action
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.common.capability.disk.DiskContentHandler
import net.asch.bulkit.common.data.ResourceIdentifier
import net.asch.bulkit.mekanism.BulkItMekanism
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidType

class DiskGasHandler(disk: ItemStack, private val filter: Filter) : IGasHandler {
    private val resourceHolder = resource(filter)
    private val diskContent = disk.getCapability(resourceHolder.disk.contentHandler)!!

    override fun getTanks(): Int = 1
    override fun getChemicalInTank(tank: Int): GasStack = toStack()
    override fun getTankCapacity(tank: Int): Long = minOf(FluidType.BUCKET_VOLUME.toLong(), diskContent.capacity)

    override fun insertChemical(tank: Int, stack: GasStack, action: Action): GasStack {
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

        return if (amountToInsert == stack.amount) emptyStack else (stack.copyWithAmount(amountToInsert))
    }

    override fun extractChemical(tank: Int, amount: Long, action: Action): GasStack {
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

    override fun setChemicalInTank(tank: Int, stack: GasStack) {
        if (diskContent.id == null) {
            diskContent.id = toIdentifier(stack)
        }
    }

    private fun toIdentifier(stack: GasStack): ResourceIdentifier<Gas> =
        ResourceIdentifier(stack.chemicalHolder, DataComponentPatch.EMPTY)

    private fun toStack(amount: Long): GasStack =
        diskContent.id?.let { GasStack(it.resource, amount) } ?: GasStack.EMPTY

    private fun toStack(): GasStack = toStack(diskContent.amount)

    override fun isValid(tank: Int, stack: GasStack): Boolean =
        diskContent.canInsertResource(toIdentifier(stack)) && when (filter) {
            Filter.ALL -> true
            Filter.ONLY_NON_RADIOACTIVE -> !stack.isRadioactive
            Filter.ONLY_RADIOACTIVE -> stack.isRadioactive
        }

    enum class Filter {
        ALL, ONLY_NON_RADIOACTIVE, ONLY_RADIOACTIVE
    }

    companion object {
        fun createOnlyNonRadioactive(disk: ItemStack, ctx: Void): IGasHandler =
            DiskGasHandler(disk, Filter.ONLY_NON_RADIOACTIVE)

        fun createOnlyRadioactive(disk: ItemStack, ctx: Void): IGasHandler =
            DiskGasHandler(disk, Filter.ONLY_RADIOACTIVE)

        fun resource(filter: Filter): ResourceHolder<Gas, IGasHandler, IGasHandler, DiskContentHandler<Gas>> =
            when (filter) {
                Filter.ALL -> TODO()
                Filter.ONLY_NON_RADIOACTIVE -> BulkItMekanism.RESOURCE_GAS_NON_RADIOACTIVE
                Filter.ONLY_RADIOACTIVE -> BulkItMekanism.RESOURCE_GAS_RADIOACTIVE
            }
    }
}