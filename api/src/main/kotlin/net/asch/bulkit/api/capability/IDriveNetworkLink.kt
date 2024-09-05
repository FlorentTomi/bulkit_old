package net.asch.bulkit.api.capability

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface IDriveNetworkLink {
    fun unmap(slot: Int) = map(slot, UNMAPPED_SLOT)
    fun map(slot: Int, rootSlot: Int)

    fun unlink(player: Player) = linkTo(player, null)
    fun linkTo(player: Player, blockPos: BlockPos?)

    fun disk(slot: Int): ItemStack

    companion object {
        const val UNMAPPED_SLOT: Int = -1
    }
}