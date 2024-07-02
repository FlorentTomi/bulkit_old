package net.asch.bulkit.common.capability

import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.data.ResourceIdentifier
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

abstract class DiskContentHandler<R>(
    private val disk: ItemStack, protected val resourceHolder: ResourceHolder<R, *, *, *>
) {
    var id: ResourceIdentifier<R>?
        get() = disk.get(resourceHolder.id)
        set(value) {
            disk.set(resourceHolder.id, value)
            amount = 0
        }

    var amount: Long
        get() = disk.getOrDefault(DataComponents.Disk.AMOUNT, 0)
        set(value) {
            disk.set(DataComponents.Disk.AMOUNT, value)
        }

    var locked: Boolean
        get() = disk.getOrDefault(DataComponents.Disk.LOCKED, true)
        set(value) {
            disk.set(DataComponents.Disk.LOCKED, value)
        }

    var void: Boolean
        get() = disk.getOrDefault(DataComponents.Disk.VOID, false)
        set(value) {
            disk.set(DataComponents.Disk.VOID, value)
        }

    abstract val maxStackSize: Int
    abstract val capacity: Long
    abstract val description: Component

    fun canInsertResource(resourceToInsert: ResourceIdentifier<R>): Boolean = (id == null) || (id == resourceToInsert)

    fun multiplier(defaultMultiplier: Int): Int {
        var multiplier = 1
        var hasDowngrade = false

        val mods = disk.getCapability(Capabilities.DISK_MODS) ?: return multiplier
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