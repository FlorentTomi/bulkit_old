package net.asch.bulkit.client.capability

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.DiskResourceRenderer
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.client.text.LangEntries
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.capability.disk.DiskEnergyHandler
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

class DiskResourceEnergyRenderer(disk: ItemStack) : DiskResourceRenderer<Unit>(disk, Resources.ENERGY.get()) {
    override fun getResourceCapacity(id: ResourceIdentifier<Unit>, handler: IDiskResourceHandler): Long =
        DiskEnergyHandler.capacity(DiskEnergyHandler.BASE_CAPACITY, handler).toLong()

    override fun getResourceDescription(resourceId: ResourceIdentifier<Unit>): Component =
        LangEntries.RESOURCE_ENERGY.component()

    override fun renderResource(
        resourceId: ResourceIdentifier<Unit>, amount: Long, guiGraphics: GuiGraphics, size: Int
    ) {

    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun build(stack: ItemStack, ctx: Void) = DiskResourceEnergyRenderer(stack)
    }
}