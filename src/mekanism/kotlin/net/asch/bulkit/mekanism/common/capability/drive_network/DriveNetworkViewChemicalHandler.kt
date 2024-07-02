package net.asch.bulkit.mekanism.common.capability.drive_network

import mekanism.api.Action
import mekanism.api.chemical.Chemical
import mekanism.api.chemical.ChemicalStack
import mekanism.api.chemical.IChemicalHandler
import net.asch.bulkit.common.capability.drive_network.DriveNetworkViewStorage
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.fluids.FluidType

abstract class DriveNetworkViewChemicalHandler<C : Chemical<C>, S : ChemicalStack<C>, H : IChemicalHandler<C, S>>(
    blockEntity: BlockEntity, diskCapability: ItemCapability<H, Void>
) : DriveNetworkViewStorage<H>(blockEntity, diskCapability), IChemicalHandler<C, S> {
    override fun getTanks(): Int = nSlots
    override fun getChemicalInTank(tank: Int): S = execute(tank, IChemicalHandler<C, S>::getChemicalInTank, emptyStack)

    override fun getTankCapacity(tank: Int): Long =
        execute(tank, IChemicalHandler<C, S>::getTankCapacity, FluidType.BUCKET_VOLUME.toLong())

    override fun isValid(tank: Int, stack: S): Boolean = execute(tank, IChemicalHandler<C, S>::isValid, false, stack)

    override fun insertChemical(tank: Int, stack: S, action: Action): S =
        execute(tank, IChemicalHandler<C, S>::insertChemical, stack, stack, action)

    override fun extractChemical(tank: Int, amount: Long, action: Action): S =
        execute(tank, IChemicalHandler<C, S>::extractChemical, emptyStack, amount, action)

    override fun setChemicalInTank(tank: Int, stack: S) =
        execute(tank, IChemicalHandler<C, S>::setChemicalInTank, Unit, stack)
}