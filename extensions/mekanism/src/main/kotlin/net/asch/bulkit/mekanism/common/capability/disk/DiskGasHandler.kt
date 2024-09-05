package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.Action
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.mekanism.BulkIt
import net.asch.bulkit.mekanism.common.data.extensions.identifier
import net.asch.bulkit.mekanism.common.data.extensions.of
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidType

class DiskGasHandler(private val disk: ItemStack, private val resourceType: ResourceType<Gas>) : IGasHandler {
    private val resource = disk.getCapability(Capabilities.Disk.RESOURCE)!!
    private var id: ResourceIdentifier<Gas>?
        get() = disk.get(resourceType.id)
        set(value) {
            disk.set(resourceType.id, value)
            resource.amount = 0
        }

    private val capacity: Long
        get() = capacity(resource).toLong()

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
        val amountToInsert = if (!resource.isVoidExcess) minOf(remainingCapacity, stack.amount) else stack.amount
        if (amountToInsert == 0L) {
            return stack
        }

        if (action.execute()) {
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
            if (action.execute()) {
                if (!resource.isLocked) {
                    id = null
                } else {
                    resource.amount = 0
                }
            }

            return existing
        }

        if (action.execute()) {
            resource.amount -= toExtract
        }

        return toStack(toExtract)
    }

    override fun setChemicalInTank(tank: Int, stack: GasStack) {
        if (id == null) {
            id = toIdentifier(stack)
        }
    }

    override fun isValid(tank: Int, stack: GasStack): Boolean = when (resourceType) {
        BulkIt.GAS_NON_RADIOACTIVE.get() -> !stack.isRadioactive
        BulkIt.GAS_RADIOACTIVE.get() -> stack.isRadioactive
        else -> false
    }

    private fun toIdentifier(stack: GasStack): ResourceIdentifier<Gas> = stack.identifier()
    private fun toStack(amount: Long): GasStack = id?.of(amount) ?: GasStack.EMPTY
    private fun toStack(): GasStack = toStack(resource.amount)

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER = 32

        fun capacity(resource: IDiskResourceHandler): Int =
            FluidType.BUCKET_VOLUME * resource.getMultiplier(DEFAULT_CAPACITY_MULTIPLIER)

        @Suppress("UNUSED_PARAMETER")
        fun buildOnlyNonRadioactive(disk: ItemStack, ctx: Void?): IGasHandler =
            DiskGasHandler(disk, BulkIt.GAS_NON_RADIOACTIVE.get())

        @Suppress("UNUSED_PARAMETER")
        fun buildOnlyRadioactive(disk: ItemStack, ctx: Void?): IGasHandler =
            DiskGasHandler(disk, BulkIt.GAS_RADIOACTIVE.get())
    }
}