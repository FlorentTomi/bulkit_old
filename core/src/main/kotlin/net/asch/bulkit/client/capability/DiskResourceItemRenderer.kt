package net.asch.bulkit.client.capability

import net.asch.bulkit.api.capability.DiskResourceRenderer
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.capability.disk.DiskItemHandler
import net.asch.bulkit.common.data.extensions.of
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DiskResourceItemRenderer(disk: ItemStack) : DiskResourceRenderer<Item>(
    disk, Resources.ITEM.get()
) {
    override fun getResourceCapacity(id: ResourceIdentifier<Item>, handler: IDiskResourceHandler): Long =
        DiskItemHandler.capacity(id.resource.value().defaultMaxStackSize, handler).toLong()

    override fun getResourceDescription(id: ResourceIdentifier<Item>): Component = id.of(1).hoverName

    override fun renderResource(
        id: ResourceIdentifier<Item>, handler: IDiskResourceHandler, guiGraphics: GuiGraphics, size: Int
    ) {
        val stack = id.of(handler.amountI)
        renderItem(stack, guiGraphics, size)
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun build(stack: ItemStack, ctx: Void?) = DiskResourceItemRenderer(stack)
    }
}