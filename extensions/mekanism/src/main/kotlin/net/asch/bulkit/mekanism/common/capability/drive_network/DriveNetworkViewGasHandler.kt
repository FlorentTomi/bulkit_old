package net.asch.bulkit.mekanism.common.capability.drive_network

import mekanism.api.Action
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.asch.bulkit.mekanism.BulkIt
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity

class DriveNetworkViewGasHandler(blockEntity: BlockEntity, filter: BulkIt.GasFilter, direction: Direction) :
    IGasHandler {
    private val resourceType = BulkIt.gasResource(filter).get()
    private val nSlots = blockEntity.blockState.getValue(DriveNetworkViewBase.N_SLOTS_STATE)
    private val link: IDriveNetworkLink? = blockEntity.level?.getCapability(
        Capabilities.DriveNetwork.LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, direction
    )

    override fun getTanks(): Int = nSlots
    override fun getChemicalInTank(tank: Int): GasStack =
        link?.disk(tank)?.getCapability(CAP)?.getChemicalInTank(0) ?: GasStack.EMPTY

    override fun getTankCapacity(tank: Int): Long = link?.disk(tank)?.getCapability(CAP)?.getTankCapacity(0) ?: 0

    override fun isValid(tank: Int, stack: GasStack): Boolean =
        link?.disk(tank)?.getCapability(CAP)?.isValid(0, stack) ?: false

    override fun insertChemical(tank: Int, stack: GasStack, action: Action): GasStack =
        link?.disk(tank)?.getCapability(CAP)?.insertChemical(0, stack, action) ?: stack

    override fun extractChemical(tank: Int, amount: Long, action: Action): GasStack =
        link?.disk(tank)?.getCapability(CAP)?.extractChemical(0, amount, action) ?: emptyStack

    override fun setChemicalInTank(tank: Int, stack: GasStack) {
        link?.disk(tank)?.getCapability(CAP)?.setChemicalInTank(0, stack)
    }

    companion object {
        private val CAP = mekanism.common.capabilities.Capabilities.GAS.item

        fun buildOnlyNonRadioactive(blockEntity: BlockEntity, ctx: Direction): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, BulkIt.GasFilter.ONLY_NON_RADIOACTIVE, ctx)

        fun buildOnlyRadioactive(blockEntity: BlockEntity, ctx: Direction): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, BulkIt.GasFilter.ONLY_RADIOACTIVE, ctx)
    }
}