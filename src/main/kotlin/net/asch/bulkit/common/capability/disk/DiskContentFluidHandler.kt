package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.data.ResourceIdentifier
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidType

class DiskContentFluidHandler(disk: ItemStack, ctx: Void) : DiskContentHandler(disk) {
    var id: ResourceIdentifier<Fluid>?
        get() = disk.get(BulkIt.RESOURCE_FLUID.id)
        set(value) {
            disk.set(BulkIt.RESOURCE_FLUID.id, value)
            amount = 0
        }

    override val maxStackSize: Int = FluidType.BUCKET_VOLUME
    override val capacity: Long
        get() = maxStackSize.toLong() * multiplier(DEFAULT_CAPACITY_MULTIPLIER)
    override val description: Component
        get() = id?.resource?.value()?.fluidType?.description ?: Component.empty()

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32
    }
}