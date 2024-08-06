package net.asch.bulkit.common.menu

import net.asch.bulkit.api.capability.Capabilities
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class DiskMenu(containerId: Int, playerInventory: Inventory) : AbstractContainerMenu(Menus.DISK.get(), containerId) {
    val disk = playerInventory.getItem(playerInventory.selected)

    init {
        disk.getCapability(Capabilities.Disk.MODS)?.let(::initializeModSlots)
    }

    override fun quickMoveStack(player: Player, slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun stillValid(player: Player): Boolean = true

    @Suppress("UNUSED_PARAMETER")
    private fun initializeModSlots(mods: IItemHandler) {

    }
}