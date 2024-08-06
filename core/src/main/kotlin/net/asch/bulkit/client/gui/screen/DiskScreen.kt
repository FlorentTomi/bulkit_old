package net.asch.bulkit.client.gui.screen

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceRenderer
import net.asch.bulkit.client.gui.screen.utils.ConsoleGui
import net.asch.bulkit.common.menu.DiskMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

class DiskScreen(menu: DiskMenu, playerInventory: Inventory, title: Component) : AbstractContainerScreen<DiskMenu>(
    menu, playerInventory, title
) {
    private val disk: ItemStack = playerInventory.getItem(playerInventory.selected)
    private val renderer: IDiskResourceRenderer? = disk.getCapability(Capabilities.Disk.RESOURCE_RENDERER)

    init {
        imageWidth = WIDTH
        imageHeight = HEIGHT
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        ConsoleGui.drawBackground(guiGraphics, leftPos, topPos, WIDTH, HEADER_HEIGHT)
        ConsoleGui.drawBackground(guiGraphics, leftPos, topPos + BODY_OFFSET, WIDTH, BODY_HEIGHT)
    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

        ConsoleGui.renderDiskHeader(renderer, pGuiGraphics, leftPos, topPos, WIDTH, HEADER_HEIGHT)
    }
}

private const val HEADER_HEIGHT = 44
private const val BODY_OFFSET = HEADER_HEIGHT + 4
private const val BODY_HEIGHT = 128
private const val WIDTH = 200
private const val HEIGHT = BODY_OFFSET + BODY_HEIGHT
