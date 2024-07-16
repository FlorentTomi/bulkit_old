package net.asch.bulkit.api.capability

import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack

interface IDriveNetworkLink {
    fun unmap(slot: Int) = map(slot, UNMAPPED_SLOT)
    fun map(slot: Int, rootSlot: Int)

    fun unlink() = linkTo(null)
    fun linkTo(blockPos: BlockPos?)

    fun disk(slot: Int): ItemStack

    companion object {
        const val UNMAPPED_SLOT: Int = -1
    }
}