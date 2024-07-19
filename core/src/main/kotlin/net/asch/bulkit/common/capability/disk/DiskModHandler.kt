package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.BaseMod
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ComponentItemHandler

class DiskModHandler(stack: ItemStack) :
    ComponentItemHandler(stack, DataComponents.Disk.MODS.get(), 4) {
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return super.isItemValid(slot, stack) && (stack.item is BaseMod)
    }
}