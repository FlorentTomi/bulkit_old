package net.asch.bulkit.client.gui.screen.utils

import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.capability.IDiskResourceRenderer
import net.asch.bulkit.client.text.LangEntries
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot
import java.awt.Color

object ConsoleGui {
    const val OUTER_MARGIN: Int = 4
    const val MARGIN: Int = 4
    const val SPACING: Int = 4
    const val SLOT_SIZE: Int = 16
    const val SEPARATOR_WIDTH: Int = 4

    fun drawBackground(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) {
        var currentX = x
        var currentY = y
        var currentWidth = width
        var currentHeight = height

        guiGraphics.fill(currentX, currentY, currentX + currentWidth, currentY + currentHeight, Color.white.rgb)

        var offset = 1
        currentX += offset
        currentY += offset
        currentWidth -= 2 * offset
        currentHeight -= 2 * offset
        guiGraphics.fill(currentX, currentY, currentX + currentWidth, currentY + currentHeight, Color.lightGray.rgb)

        offset = 2
        currentX += offset
        currentY += offset
        currentWidth -= 2 * offset
        currentHeight -= 2 * offset
        guiGraphics.fill(currentX, currentY, currentX + currentWidth, currentY + currentHeight, Color.gray.rgb)

        offset = 1
        currentX += offset
        currentY += offset
        currentWidth -= 2 * offset
        currentHeight -= 2 * offset
        guiGraphics.fill(currentX, currentY, currentX + currentWidth, currentY + currentHeight, Color.black.rgb)
    }

    fun drawBackgroundSeparator(guiGraphics: GuiGraphics, x: Int, y: Int, height: Int) {
        guiGraphics.fill(x, y + 3, x + SEPARATOR_WIDTH, y + height - 3, Color.gray.rgb)
        guiGraphics.fill(x + 1, y + 3, x + SEPARATOR_WIDTH - 1, y + height - 3, Color.lightGray.rgb)
    }

    fun drawSlot(slot: Slot, guiGraphics: GuiGraphics, xOffset: Int, yOffset: Int) {
        if (slot.item.isEmpty) {
            guiGraphics.fill(
                slot.x + xOffset,
                slot.y + yOffset,
                slot.x + xOffset + SLOT_SIZE,
                slot.y + yOffset + SLOT_SIZE,
                Color.gray.rgb
            )
        }
    }

    fun renderDiskHeader(
        renderer: IDiskResourceRenderer?, guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int
    ) {
        val contentWidth = height - (OUTER_MARGIN + MARGIN) * 2
        drawDiskContent(renderer, guiGraphics, x + OUTER_MARGIN + MARGIN, y + OUTER_MARGIN + MARGIN, contentWidth)

        if (renderer == null) {
            return
        }

        guiGraphics.drawWordWrap(
            Minecraft.getInstance().font,
            renderer.description,
            x + OUTER_MARGIN + MARGIN + contentWidth + SPACING,
            y + OUTER_MARGIN + MARGIN,
            width - contentWidth - SPACING - (MARGIN + OUTER_MARGIN) * 2,
            Color.green.rgb
        )
    }

    fun renderDiskBody(
        resource: IDiskResourceHandler?, guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int
    ) {
        val innerX = x + OUTER_MARGIN + MARGIN
        val innerY = y + OUTER_MARGIN + MARGIN
        val innerWidth = width - OUTER_MARGIN + MARGIN * 2
        val innerHeight = height - (OUTER_MARGIN + MARGIN) * 2

        guiGraphics.pose().pushPose()

        val font = Minecraft.getInstance().font
        val offset = font.lineHeight + SPACING
//        var currentY = innerY

        guiGraphics.drawString(
            font, LangEntries.SCREEN_DISK_AMOUNT.component(resource?.amount ?: 0), innerX, innerY, Color.green.rgb
        )

//        currentY += offset
//        guiGraphics.drawString(
//            font, LangEntries.SCREEN_DISK_LOCKED.component(), innerX, currentY, Color.green.rgb
//        )
//
//        currentY += offset
//        guiGraphics.drawString(
//            font, LangEntries.SCREEN_DISK_VOID.component(), innerX, currentY, Color.green.rgb
//        )

        guiGraphics.pose().popPose()
    }

    private fun drawDiskContent(renderer: IDiskResourceRenderer?, guiGraphics: GuiGraphics, x: Int, y: Int, size: Int) {
        guiGraphics.fill(x, y, x + size, y + size, Color.green.rgb)
        guiGraphics.fill(x + 1, y + 1, x + size - 1, y + size - 1, Color.white.rgb)
        renderer?.render(guiGraphics, x + 2, y + 2, size - 4)
    }
}