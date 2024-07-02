package net.asch.bulkit.mekanism.common.capability.disk

import mekanism.api.chemical.gas.Gas
import net.asch.bulkit.common.capability.DiskContentHandler
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler.Filter
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidType

class DiskContentGasHandler(disk: ItemStack, filter: Filter) :
    DiskContentHandler<Gas>(disk, DiskGasHandler.resource(filter)) {
    override val maxStackSize: Int = FluidType.BUCKET_VOLUME
    override val capacity: Long
        get() = maxStackSize.toLong() * multiplier(DEFAULT_CAPACITY_MULTIPLIER)
    override val description: Component
        get() = id?.resource?.value()?.chemical?.textComponent ?: Component.empty()

    companion object {
        private const val DEFAULT_CAPACITY_MULTIPLIER: Int = 32

        fun createOnlyNonRadioactive(disk: ItemStack, ctx: Void): DiskContentHandler<Gas> =
            DiskContentGasHandler(disk, Filter.ONLY_NON_RADIOACTIVE)

        fun createOnlyRadioactive(disk: ItemStack, ctx: Void): DiskContentHandler<Gas> =
            DiskContentGasHandler(disk, Filter.ONLY_RADIOACTIVE)
    }
}