package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.capability.DiskContentHandler
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DiskContentItemHandler(disk: ItemStack, ctx: Void) : DiskContentHandler<Item>(disk, BulkIt.RESOURCE_ITEM) {
    override val maxStackSize: Int
        get() = id?.resource?.value()?.defaultMaxStackSize ?: 64
    override val capacity: Long
        get() = maxStackSize.toLong() * multiplier(DEFAULT_CAPACITY_MULTIPLIER)
    override val description: Component
        get() = id?.resource?.value()?.description ?: Component.empty()

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 8
    }
}