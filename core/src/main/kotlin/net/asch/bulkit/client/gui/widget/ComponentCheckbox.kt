package net.asch.bulkit.client.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import net.asch.bulkit.api.BulkItApi
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.core.component.DataComponentType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

class ComponentCheckbox(
    private val dataComponentType: DataComponentType<Boolean>,
    x: Int,
    y: Int,
    message: Component,
    font: Font,
    private val onPressFn: OnPress
) : AbstractButton(x, y, 0, 0, message) {
    private val defaultWidth: Int = BOX_SIZE + 4 + font.width(message)
    private val textWidget: MultiLineTextWidget
    private var selected: Boolean = false

    init {
        width = defaultWidth
        textWidget = MultiLineTextWidget(message, font).setMaxWidth(width).setColor(TEXT_COLOR)
        height = maxOf(BOX_SIZE, textWidget.height)
    }

    fun update(stack: ItemStack) {
        active = stack.has(dataComponentType)
        selected = stack.getOrDefault(dataComponentType, false)
//        BulkItApi.logInfo("active=$active, selected=$selected")
    }

    override fun onPress() {
        onPressFn(!selected)
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage())
        if (this.active) {
            if (this.isFocused) {
                pNarrationElementOutput.add(
                    NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused")
                )
            } else {
                pNarrationElementOutput.add(
                    NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered")
                )
            }
        }
    }

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        RenderSystem.enableDepthTest()
        pGuiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.enableBlend()

        val checkboxSprite = if (selected) CHECKBOX_SELECTED_SPRITE else CHECKBOX_SPRITE

        pGuiGraphics.blitSprite(checkboxSprite, x, y, BOX_SIZE, BOX_SIZE)
        textWidget.setPosition(x + BOX_SIZE + 4, y + BOX_SIZE / 2 - textWidget.height / 2)
        textWidget.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    class Builder(
        private val dataComponentType: DataComponentType<Boolean>,
        private val message: Component,
        private val font: Font
    ) {
        private var x = 0
        private var y = 0
        private var onPressFn: OnPress = {}

        fun pos(x: Int, y: Int): Builder {
            this.x = x
            this.y = y
            return this
        }

        fun onPress(onPressFn: OnPress): Builder {
            this.onPressFn = onPressFn
            return this
        }

        fun build(): ComponentCheckbox = ComponentCheckbox(dataComponentType, x, y, message, font, onPressFn)
    }

}

typealias OnPress = (Boolean) -> Unit

private const val BOX_SIZE = 17
private const val TEXT_COLOR = 14737632
private val CHECKBOX_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected")
private val CHECKBOX_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox")