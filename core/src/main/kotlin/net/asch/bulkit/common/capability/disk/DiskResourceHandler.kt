package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.data.delegate.DefaultedComponentDelegate
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import kotlin.reflect.KClass

class DiskResourceHandler(private val disk: ItemStack) : IDiskResourceHandler {
    override var amountL: Long by DefaultedComponentDelegate(disk, DataComponents.DISK_AMOUNT, 0)
    override var amountI: Int
        get() = amountL.toInt()
        set(value) {
            amountL = value.toLong()
        }

    override var isLocked: Boolean by DefaultedComponentDelegate(disk, DataComponents.DISK_LOCKED, false)
    override var isVoidExcess: Boolean by DefaultedComponentDelegate(disk, DataComponents.DISK_VOID, false)
    override val mods: IItemHandler?
        get() = disk.getCapability(Capabilities.Disk.MODS)

    override fun getMultiplier(defaultMultiplier: Int): Int {
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
}