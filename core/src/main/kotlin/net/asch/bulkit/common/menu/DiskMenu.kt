package net.asch.bulkit.common.menu

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.client.gui.screen.DiskScreen
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler

class DiskMenu(containerId: Int, playerInventory: Inventory) : AbstractContainerMenu(Menus.DISK.get(), containerId) {
    val disk = playerInventory.getItem(playerInventory.selected)
    private val mods: IItemHandler? = disk.getCapability(Capabilities.Disk.MODS)

    init {
        DiskScreen.initializeMenuSlots(this, mods?.slots ?: 0)
    }

    override fun quickMoveStack(player: Player, slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun stillValid(player: Player): Boolean = true

    fun addModSlot(slot: Int, x: Int, y: Int) {
        mods?.let { SlotItemHandler(it, slot, x, y) }
            ?.let { addSlot(it) }
    }
}