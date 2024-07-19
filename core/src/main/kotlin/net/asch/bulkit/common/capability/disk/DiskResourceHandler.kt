package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.data.delegate.DefaultedComponentDelegate
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class DiskResourceHandler(private val disk: ItemStack) : IDiskResourceHandler {
    private var thisAmount: Long by DefaultedComponentDelegate(disk, DataComponents.Disk.AMOUNT, 0L)
    private var thisLocked: Boolean by DefaultedComponentDelegate(disk, DataComponents.Disk.LOCKED, true)
    private var thisVoid: Boolean by DefaultedComponentDelegate(disk, DataComponents.Disk.VOID, false)
    private val thisMods: IItemHandler?
        get() = disk.getCapability(Capabilities.Disk.MODS)

    override fun getAmount(): Long = thisAmount
    override fun setAmount(value: Long) {
        thisAmount = value
    }

    override fun isLocked(): Boolean = thisLocked
    override fun setLocked(value: Boolean) {
        thisLocked = value
    }

    override fun isVoidExcess(): Boolean = thisVoid
    override fun setVoidExcess(value: Boolean) {
        thisVoid = value
    }

    override fun mods(): IItemHandler? = thisMods

    override fun multiplier(defaultMultiplier: Int): Int {
        var multiplier = 1
        var hasDowngrade = false

        val mods = thisMods ?: return multiplier
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
        fun build(stack: ItemStack, ctx: Void): IDiskResourceHandler = DiskResourceHandler(stack)
    }
}