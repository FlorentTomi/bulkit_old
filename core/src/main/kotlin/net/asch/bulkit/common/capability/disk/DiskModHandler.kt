package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.BaseMod
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ComponentItemHandler

class DiskModHandler(stack: ItemStack) :
    ComponentItemHandler(stack, DataComponents.DISK_MODS.get(), SIZE) {
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return super.isItemValid(slot, stack) && (stack.isEmpty || stack.item is BaseMod)
    }

    companion object {
        const val SIZE: Int = 4
    }
}