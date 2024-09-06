package net.asch.bulkit.mekanism.client.capability

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import mekanism.api.chemical.gas.Gas
import mekanism.client.render.MekanismRenderer
import net.asch.bulkit.api.capability.DiskResourceRenderer
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.mekanism.BulkIt
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.data.extensions.of
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack


class DiskResourceGasRenderer(disk: ItemStack, resourceType: ResourceType<Gas>) :
    DiskResourceRenderer<Gas>(disk, resourceType) {
    override fun getResourceCapacity(id: ResourceIdentifier<Gas>, handler: IDiskResourceHandler): Long =
        DiskGasHandler.capacity(handler)

    override fun getResourceDescription(id: ResourceIdentifier<Gas>): Component = id.of(1).textComponent

    override fun renderResource(
        id: ResourceIdentifier<Gas>, handler: IDiskResourceHandler, guiGraphics: GuiGraphics, size: Int
    ) {
        val gas = id.resource.value()

        val sprite = MekanismRenderer.getChemicalTexture(gas)
        MekanismRenderer.color(guiGraphics, gas)

        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS)

        val pose = guiGraphics.pose()
        pose.pushPose()
//        RenderSystem.setShaderColor(red, green, blue, 1.0f)

        val zLevel = 0
        val uMin = sprite.u0
        val uMax = sprite.u1
        val vMin = sprite.v0
        val vMax = sprite.v1
        val textureWidth = sprite.contents().width()
        val textureHeight = sprite.contents().height()

        val tesselator = Tesselator.getInstance()
        val vertexBuffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        var yOffset = 0
        while (yOffset < size) {
            val drawHeight = minOf(textureHeight, size - yOffset)
            val drawY = yOffset // - drawHeight

            val vMaxAdjusted = vMin + (vMax - vMin) * (drawHeight.toFloat() / textureHeight)

            var xOffset = 0
            while (xOffset < size) {
                val drawWidth = minOf(textureWidth, size - xOffset)

                val uMaxAdjusted = uMin + (uMax - uMin) * (drawWidth.toFloat() / textureWidth)

                vertexBuffer.addVertex(
                    pose.last().pose(), xOffset.toFloat(), (drawY + drawHeight).toFloat(), zLevel.toFloat()
                ).setUv(uMin, vMaxAdjusted)

                vertexBuffer.addVertex(
                    pose.last().pose(),
                    (xOffset + drawWidth).toFloat(),
                    (drawY + drawHeight).toFloat(),
                    zLevel.toFloat()
                ).setUv(uMaxAdjusted, vMaxAdjusted)

                vertexBuffer.addVertex(
                    pose.last().pose(), (xOffset + drawWidth).toFloat(), drawY.toFloat(), zLevel.toFloat()
                ).setUv(uMaxAdjusted, vMin)

                vertexBuffer.addVertex(pose.last().pose(), xOffset.toFloat(), drawY.toFloat(), zLevel.toFloat())
                    .setUv(uMin, vMin)

                xOffset += drawWidth
            }
            yOffset += drawHeight
        }

        BufferUploader.drawWithShader(vertexBuffer.buildOrThrow())
        pose.popPose()
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.applyModelViewMatrix()
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun buildRadioactive(stack: ItemStack, ctx: Void?) =
            DiskResourceGasRenderer(stack, BulkIt.GAS_RADIOACTIVE.get())

        fun buildNonRadioactive(stack: ItemStack, ctx: Void?) =
            DiskResourceGasRenderer(stack, BulkIt.GAS_NON_RADIOACTIVE.get())
    }
}