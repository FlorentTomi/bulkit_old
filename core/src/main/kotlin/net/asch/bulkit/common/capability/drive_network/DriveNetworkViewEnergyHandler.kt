package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDriveNetworkLink
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.energy.IEnergyStorage

class DriveNetworkViewEnergyHandler(blockEntity: BlockEntity, ctx: Direction) : IEnergyStorage {
    private val link: IDriveNetworkLink? = blockEntity.level?.getCapability(
        Capabilities.DriveNetwork.LINK, blockEntity.blockPos, blockEntity.blockState, blockEntity, ctx
    )

    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int =
        link?.disk(0)?.getCapability(CAP)?.receiveEnergy(toReceive, simulate) ?: 0

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int =
        link?.disk(0)?.getCapability(CAP)?.extractEnergy(toExtract, simulate) ?: 0

    override fun getEnergyStored(): Int = link?.disk(0)?.getCapability(CAP)?.energyStored ?: 0
    override fun getMaxEnergyStored(): Int = link?.disk(0)?.getCapability(CAP)?.maxEnergyStored ?: 0
    override fun canExtract(): Boolean = link?.disk(0)?.getCapability(CAP)?.canExtract() ?: false
    override fun canReceive(): Boolean = link?.disk(0)?.getCapability(CAP)?.canReceive() ?: false

    companion object {
        private val CAP = net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM

        fun build(blockEntity: BlockEntity, ctx: Direction) = DriveNetworkViewEnergyHandler(blockEntity, ctx)
    }
}