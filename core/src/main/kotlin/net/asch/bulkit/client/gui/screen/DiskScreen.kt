package net.asch.bulkit.client.gui.screen

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.capability.ClientCapabilities
import net.asch.bulkit.api.capability.IDiskResourceRenderer
import net.asch.bulkit.client.gui.screen.utils.ConsoleGui
import net.asch.bulkit.client.gui.widget.ComponentCheckbox
import net.asch.bulkit.client.text.LangEntries
import net.asch.bulkit.common.capability.disk.DiskModHandler
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.menu.DiskMenu
import net.asch.bulkit.network.DiskPayloads
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

class DiskScreen(menu: DiskMenu, private val playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<DiskMenu>(
        menu, playerInventory, title
    ) {
    private val disk: ItemStack
        get() = playerInventory.getItem(playerInventory.selected)
    private val resource: IDiskResourceHandler? = disk.getCapability(Capabilities.Disk.RESOURCE)
    private val renderer: IDiskResourceRenderer? = disk.getCapability(
        ClientCapabilities.RESOURCE_RENDERER)
    private val lockedCheckbox: ComponentCheckbox = ComponentCheckbox.Builder(
        DataComponents.DISK_LOCKED.get(), LangEntries.SCREEN_DISK_LOCKED.component(), Minecraft.getInstance().font
    ).onPress(DiskPayloads::lock).build()

    init {
        imageWidth = WIDTH
        imageHeight = HEIGHT
    }

    override fun init() {
        super.init()

        lockedCheckbox.setPosition(
            leftPos + ConsoleGui.OUTER_MARGIN + ConsoleGui.MARGIN,
            topPos + BODY_OFFSET + ConsoleGui.OUTER_MARGIN + ConsoleGui.MARGIN + font.lineHeight + ConsoleGui.SPACING
        )
        addRenderableWidget(lockedCheckbox)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        ConsoleGui.drawBackground(guiGraphics, leftPos, topPos, WIDTH, HEADER_HEIGHT)
        ConsoleGui.drawBackground(guiGraphics, leftPos, topPos + BODY_OFFSET, WIDTH, BODY_HEIGHT)
        ConsoleGui.drawBackgroundSeparator(
            guiGraphics, leftPos + MOD_SEPARATOR_OFFSET, topPos + BODY_OFFSET, BODY_HEIGHT
        )
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {

    }

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)

        ConsoleGui.renderDiskHeader(renderer, pGuiGraphics, leftPos, topPos, WIDTH, HEADER_HEIGHT)
        ConsoleGui.renderDiskBody(
            resource, pGuiGraphics, leftPos, topPos + BODY_OFFSET, MOD_SEPARATOR_OFFSET, BODY_HEIGHT
        )
        menu.slots.forEach { slot -> ConsoleGui.drawSlot(slot, pGuiGraphics, leftPos, topPos) }
    }

    override fun containerTick() {
        super.containerTick()
        lockedCheckbox.update(disk)
    }

    companion object {
        fun initializeMenuSlots(menu: DiskMenu, slots: Int) {
            for (slot in 0 until slots) {
                menu.addModSlot(
                    slot,
                    MOD_SEPARATOR_OFFSET + ConsoleGui.SEPARATOR_WIDTH + ConsoleGui.MARGIN,
                    BODY_OFFSET + ConsoleGui.OUTER_MARGIN + ConsoleGui.MARGIN + slot * (ConsoleGui.SLOT_SIZE + ConsoleGui.SPACING)
                )
            }
        }
    }
}

private const val OUTER_SPACING = 4
private const val HEADER_HEIGHT = 44
private const val BODY_OFFSET = HEADER_HEIGHT + OUTER_SPACING
private const val BODY_HEIGHT =
    (ConsoleGui.OUTER_MARGIN + ConsoleGui.MARGIN) * 2 + DiskModHandler.SIZE * ConsoleGui.SLOT_SIZE + (DiskModHandler.SIZE - 1) * ConsoleGui.SPACING
private const val WIDTH = 200
private const val HEIGHT = BODY_OFFSET + BODY_HEIGHT
private const val MOD_SEPARATOR_OFFSET =
    WIDTH - ConsoleGui.OUTER_MARGIN - ConsoleGui.MARGIN - ConsoleGui.SLOT_SIZE - ConsoleGui.MARGIN - ConsoleGui.SEPARATOR_WIDTH
