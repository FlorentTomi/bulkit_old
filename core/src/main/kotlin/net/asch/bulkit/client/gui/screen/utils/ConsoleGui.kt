package net.asch.bulkit.client.gui.screen.utils

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.capability.IDiskResourceRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import java.awt.Color

object ConsoleGui {
    private const val MARGIN: Int = 6
    private const val SPACING: Int = 4

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

    fun renderDiskHeader(
        renderer: IDiskResourceRenderer?, guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int
    ) {
        val contentWidth = height - MARGIN * 2
        drawDiskContent(renderer, guiGraphics, x + MARGIN, y + MARGIN, contentWidth)

        if (renderer == null) {
            return
        }

        guiGraphics.drawWordWrap(
            Minecraft.getInstance().font,
            renderer.description,
            x + MARGIN + contentWidth + SPACING,
            y + MARGIN,
            width - contentWidth - SPACING - MARGIN * 2,
            Color.green.rgb
        )
    }

    private fun drawDiskContent(renderer: IDiskResourceRenderer?, guiGraphics: GuiGraphics, x: Int, y: Int, size: Int) {
        guiGraphics.fill(x, y, x + size, y + size, Color.green.rgb)
        guiGraphics.fill(x + 2, y + 2, x + size - 2, y + size - 2, Color.black.rgb)
        renderer?.render(guiGraphics, x + 2, y + 2, size - 4)
    }
}