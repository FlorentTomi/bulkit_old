package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.common.data.ResourceIdentifier
import net.asch.bulkit.mekanism.BulkItMekanism
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.world.item.ItemStack

class DiskGasHandler(disk: ItemStack, private val filter: Filter) :
    DiskChemicalHandler<Gas, GasStack>(disk, resource(filter)), IGasHandler {
    private val diskContent = disk.getCapability(resourceHolder.diskContentCapability)!!

    override fun toIdentifier(stack: GasStack): ResourceIdentifier<Gas> =
        ResourceIdentifier(stack.chemicalHolder, DataComponentPatch.EMPTY)

    override fun toStack(amount: Long): GasStack = id?.let { GasStack(it.resource, amount) } ?: GasStack.EMPTY

    override fun isValid(tank: Int, stack: GasStack): Boolean = super.isValid(tank, stack) && when (filter) {
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

        fun resource(filter: Filter): ResourceHolder<Gas, IGasHandler, IGasHandler, *> = when (filter) {
            Filter.ALL -> TODO()
            Filter.ONLY_NON_RADIOACTIVE -> BulkItMekanism.RESOURCE_GAS_NON_RADIOACTIVE
            Filter.ONLY_RADIOACTIVE -> BulkItMekanism.RESOURCE_GAS_RADIOACTIVE
        }
    }
}