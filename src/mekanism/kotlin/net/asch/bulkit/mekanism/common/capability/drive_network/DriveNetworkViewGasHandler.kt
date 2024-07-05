package net.asch.bulkit.mekanism.common.capability.drive_network

import mekanism.api.Action
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewStorage
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler.Filter
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.fluids.FluidType

class DriveNetworkViewGasHandler(blockEntity: BlockEntity, filter: Filter) : DriveNetworkViewStorage<IGasHandler>(
    blockEntity, DiskGasHandler.resource(filter).disk.resourceHandler
), IGasHandler {
    override fun getTanks(): Int = nSlots
    override fun getChemicalInTank(tank: Int): GasStack = execute(tank, IGasHandler::getChemicalInTank, emptyStack)

    override fun getTankCapacity(tank: Int): Long =
        execute(tank, IGasHandler::getTankCapacity, FluidType.BUCKET_VOLUME.toLong())

    override fun isValid(tank: Int, stack: GasStack): Boolean = execute(tank, IGasHandler::isValid, false, stack)

    override fun insertChemical(tank: Int, stack: GasStack, action: Action): GasStack =
        execute(tank, IGasHandler::insertChemical, stack, stack, action)

    override fun extractChemical(tank: Int, amount: Long, action: Action): GasStack =
        execute(tank, IGasHandler::extractChemical, emptyStack, amount, action)

    override fun setChemicalInTank(tank: Int, stack: GasStack) =
        execute(tank, IGasHandler::setChemicalInTank, Unit, stack)

    companion object {
        fun <C> createOnlyNonRadioactive(blockEntity: BlockEntity, ctx: C): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, Filter.ONLY_NON_RADIOACTIVE)

        fun <C> createOnlyRadioactive(blockEntity: BlockEntity, ctx: C): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, Filter.ONLY_RADIOACTIVE)
    }
}