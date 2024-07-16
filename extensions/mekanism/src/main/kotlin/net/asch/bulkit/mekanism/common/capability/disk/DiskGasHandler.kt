package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.Action
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.api.capability.BulkItCapabilities
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.mekanism.BulkItMekanism
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidType

class DiskGasHandler(private val disk: ItemStack, private val filter: BulkItMekanism.GasFilter) : IGasHandler {
    private val resourceType = BulkItMekanism.gasResource(filter).get()
    private val resource = disk.getCapability(BulkItCapabilities.Disk.RESOURCE)!!
    private var id: ResourceIdentifier<Gas>?
        get() = disk.get(resourceType.resource)
        set(value) {
            disk.set(resourceType.resource, value)
            resource.amount = 0
        }

    private val maxStackSize: Int
        get() = FluidType.BUCKET_VOLUME
    private val capacity: Long
        get() = maxStackSize.toLong() * resource.multiplier(DEFAULT_CAPACITY_MULTIPLIER)

    override fun getTanks(): Int = 1
    override fun getChemicalInTank(tank: Int): GasStack = toStack()
    override fun getTankCapacity(tank: Int): Long = minOf(FluidType.BUCKET_VOLUME.toLong(), capacity)

    override fun insertChemical(tank: Int, stack: GasStack, action: Action): GasStack {
        if (stack.isEmpty) {
            return emptyStack
        }

        if (!isValid(tank, stack)) {
            return stack
        }

        val remainingCapacity = capacity - resource.amount
        val amountToInsert = if (!resource.void) minOf(remainingCapacity, stack.amount) else stack.amount
        if (amountToInsert == 0L) {
            return stack
        }

        if (!action.simulate()) {
            if (id == null) {
                id = toIdentifier(stack)
            }

            resource.amount = minOf(capacity, resource.amount + amountToInsert)
        }

        return if (amountToInsert == stack.amount) emptyStack else (stack.copyWithAmount(amountToInsert))
    }

    override fun extractChemical(tank: Int, amount: Long, action: Action): GasStack {
        if (resource.amount == 0L) {
            return emptyStack
        }

        if (id == null || amount == 0L) {
            return emptyStack
        }

        val toExtract = minOf(amount, FluidType.BUCKET_VOLUME.toLong())
        if (resource.amount <= toExtract) {
            val existing = toStack()
            if (!action.simulate() && !resource.locked) {
                id = null
            }

            return existing
        }

        if (!action.simulate()) {
            resource.amount -= toExtract
        }

        return toStack(toExtract)
    }

    override fun setChemicalInTank(tank: Int, stack: GasStack) {
        if (id == null) {
            id = toIdentifier(stack)
        }
    }

    override fun isValid(tank: Int, stack: GasStack): Boolean =
        (id == null) || (id == toIdentifier(stack)) && when (filter) {
            BulkItMekanism.GasFilter.ALL -> true
            BulkItMekanism.GasFilter.ONLY_NON_RADIOACTIVE -> !stack.isRadioactive
            BulkItMekanism.GasFilter.ONLY_RADIOACTIVE -> stack.isRadioactive
        }

    private fun toIdentifier(stack: GasStack): ResourceIdentifier<Gas> =
        ResourceIdentifier(stack.chemicalHolder, DataComponentPatch.EMPTY)

    private fun toStack(amount: Long): GasStack = id?.let { GasStack(it.resource, amount) } ?: GasStack.EMPTY
    private fun toStack(): GasStack = toStack(resource.amount)

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER = 32

        fun buildOnlyNonRadioactive(disk: ItemStack, ctx: Void): IGasHandler =
            DiskGasHandler(disk, BulkItMekanism.GasFilter.ONLY_NON_RADIOACTIVE)

        fun buildOnlyRadioactive(disk: ItemStack, ctx: Void): IGasHandler =
            DiskGasHandler(disk, BulkItMekanism.GasFilter.ONLY_RADIOACTIVE)
    }
}