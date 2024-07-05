package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.data.ResourceIdentifier
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DiskContentItemHandler(disk: ItemStack, ctx: Void) : DiskContentHandler(disk) {
    var id: ResourceIdentifier<Item>?
        get() = disk.get(BulkIt.RESOURCE_ITEM.id)
        set(value) {
            disk.set(BulkIt.RESOURCE_ITEM.id, value)
            amount = 0
        }

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