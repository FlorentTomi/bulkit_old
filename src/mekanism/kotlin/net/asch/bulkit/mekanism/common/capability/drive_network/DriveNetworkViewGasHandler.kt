package net.asch.bulkit.mekanism.common.capability.drive_network

import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler.Filter
import net.minecraft.world.level.block.entity.BlockEntity

class DriveNetworkViewGasHandler(blockEntity: BlockEntity, filter: DiskGasHandler.Filter) :
    DriveNetworkViewChemicalHandler<Gas, GasStack, IGasHandler>(
        blockEntity, DiskGasHandler.resource(filter).diskCapability
    ), IGasHandler {
    override fun getEmptyStack(): GasStack = GasStack.EMPTY

    companion object {
        fun <C> createOnlyNonRadioactive(blockEntity: BlockEntity, ctx: C): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, Filter.ONLY_NON_RADIOACTIVE)

        fun <C> createOnlyRadioactive(blockEntity: BlockEntity, ctx: C): IGasHandler =
            DriveNetworkViewGasHandler(blockEntity, Filter.ONLY_RADIOACTIVE)
    }
}