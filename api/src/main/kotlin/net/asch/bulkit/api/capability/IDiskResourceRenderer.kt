package net.asch.bulkit.api.capability

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

interface IDiskResourceRenderer {
    val capacity: Long
    val description: Component

    fun render(guiGraphics: GuiGraphics, x: Int, y: Int, size: Int)
}