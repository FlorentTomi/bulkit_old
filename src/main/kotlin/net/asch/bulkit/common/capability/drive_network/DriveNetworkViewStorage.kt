package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.common.block.DriveNetworkView
import net.asch.bulkit.common.capability.Capabilities
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.ItemCapability

abstract class DriveNetworkViewStorage<H>(
    blockEntity: BlockEntity, private val diskCap: ItemCapability<H, Void>
) {
    val nSlots: Int = blockEntity.blockState.getValue(DriveNetworkView.N_SLOTS_STATE)
    private val driveNetworkHandler: DriveNetworkHandler? =
        blockEntity.level?.getCapability(Capabilities.DRIVE_NETWORK, blockEntity.blockPos, null)

    protected fun <H> forEach(cap: ItemCapability<H, Void>, fn: (H) -> Unit) = driveNetworkHandler?.forEach(cap, fn)

    protected fun <R> execute(slot: Int, fn: H.(Int) -> R, failValue: R): R = driveNetworkHandler?.rootSlot(slot)
        ?.let { driveNetworkHandler.rootStorage()?.getStackInSlot(it)?.getCapability(diskCap)?.fn(0) } ?: failValue

    protected fun <T0, R> execute(slot: Int, fn: H.(Int, T0) -> R, failValue: R, p0: T0): R =
        driveNetworkHandler?.rootSlot(slot)?.let {
            driveNetworkHandler.rootStorage()?.getStackInSlot(it)?.getCapability(diskCap)?.fn(0, p0)
        } ?: failValue

    protected fun <T0, T1, R> execute(slot: Int, fn: H.(Int, T0, T1) -> R, failValue: R, p0: T0, p1: T1): R =
        driveNetworkHandler?.rootSlot(slot)?.let {
            driveNetworkHandler.rootStorage()?.getStackInSlot(it)?.getCapability(diskCap)?.fn(0, p0, p1)
        } ?: failValue
}