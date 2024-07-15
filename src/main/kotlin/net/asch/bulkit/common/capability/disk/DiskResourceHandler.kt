package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.api.capability.BulkItCapabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.data.delegate.DefaultedComponentDelegate
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class DiskResourceHandler(private val disk: ItemStack) : IDiskResourceHandler {
    override var amount: Long by DefaultedComponentDelegate(disk, DataComponents.Disk.AMOUNT, 0L)
    override var locked: Boolean by DefaultedComponentDelegate(disk, DataComponents.Disk.LOCKED, true)
    override var void: Boolean by DefaultedComponentDelegate(disk, DataComponents.Disk.VOID, false)

    override val mods: IItemHandler?
        get() = disk.getCapability(BulkItCapabilities.Disk.MODS)

    override fun multiplier(defaultMultiplier: Int): Int {
        var multiplier = 1
        var hasDowngrade = false

        val mods = mods ?: return multiplier
        for (slot in 0 until mods.slots) {
            val mod = mods.getStackInSlot(slot)
            val modItem = mod.item
            if (modItem is CapacityUpgradeMod) {
                multiplier *= modItem.multiplier
            } else if (modItem is CapacityDowngradeMod) {
                hasDowngrade = true
            }
        }

        if (!hasDowngrade) {
            multiplier *= defaultMultiplier
        }

        return multiplier
    }

    companion object {
        fun build(stack: ItemStack, ctx: Void) = DiskResourceHandler(stack)
    }
}