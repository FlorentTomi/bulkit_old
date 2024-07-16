package net.asch.bulkit.mekanism.common.capability.drive_network

import mekanism.api.Action
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.api.block.BulkItBlockStates
import net.asch.bulkit.api.capability.BulkItCapabilities
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.asch.bulkit.mekanism.BulkItMekanism
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity

class DriveNetworkViewGasHandler(blockEntity: BlockEntity, filter: BulkItMekanism.GasFilter) : IGasHandler {
    private val resourceType = BulkItMekanism.gasResource(filter).get()
    private val nSlots = blockEntity.blockState.getValue(BulkItBlockStates.DriveNetworkView.N_SLOTS_STATE)
    private val link: IDriveNetworkLink? = blockEntity.level?.getCapability(
        BulkItCapabilities.DriveNetwork.LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, null
    )

    override fun getTanks(): Int = nSlots
    override fun getChemicalInTank(tank: Int): GasStack =
        link?.disk(tank)?.getCapability(resourceType.diskCap)?.getChemicalInTank(0) ?: GasStack.EMPTY

    override fun getTankCapacity(tank: Int): Long =
        link?.disk(tank)?.getCapability(resourceType.diskCap)?.getTankCapacity(0) ?: 0

    override fun isValid(tank: Int, stack: GasStack): Boolean =
        link?.disk(tank)?.getCapability(resourceType.diskCap)?.isValid(0, stack) ?: false

    override fun insertChemical(tank: Int, stack: GasStack, action: Action): GasStack =
        link?.disk(tank)?.getCapability(resourceType.diskCap)?.insertChemical(0, stack, action) ?: stack

    override fun extractChemical(tank: Int, amount: Long, action: Action): GasStack =
        link?.disk(tank)?.getCapability(resourceType.diskCap)?.extractChemical(0, amount, action) ?: emptyStack

    override fun setChemicalInTank(tank: Int, stack: GasStack) {
        link?.disk(tank)?.getCapability(resourceType.diskCap)?.setChemicalInTank(0, stack)
    }

    companion object {
        fun buildOnlyNonRadioactive(blockEntity: BlockEntity, ctx: Direction?): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, BulkItMekanism.GasFilter.ONLY_NON_RADIOACTIVE)

        fun buildOnlyRadioactive(blockEntity: BlockEntity, ctx: Direction?): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, BulkItMekanism.GasFilter.ONLY_RADIOACTIVE)
    }
}