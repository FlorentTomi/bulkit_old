package net.asch.bulkit.common.capability.drive_network

import net.asch.bulkit.api.item.Disk
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler

class DiskDriveStorage : ItemStackHandler(SIZE) {
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = (stack.item is Disk)

    companion object {
        const val SIZE: Int = 9
    }
}