package net.asch.bulkit.client.capability

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.asch.bulkit.api.capability.DiskResourceRenderer
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.common.Resources
import net.asch.bulkit.common.capability.disk.DiskFluidHandler
import net.asch.bulkit.common.data.extensions.of
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions

class DiskResourceFluidRenderer(disk: ItemStack) : DiskResourceRenderer<Fluid>(disk, Resources.FLUID.get()) {
    override fun getResourceCapacity(id: ResourceIdentifier<Fluid>, handler: IDiskResourceHandler): Long =
        DiskFluidHandler.capacity(handler).toLong()

    override fun getResourceDescription(resourceId: ResourceIdentifier<Fluid>): Component = resourceId.of(1).hoverName

    override fun renderResource(
        resourceId: ResourceIdentifier<Fluid>, amount: Long, guiGraphics: GuiGraphics, size: Int
    ) {
        val stack = resourceId.of(amount)

        val fluidRenderProperties = IClientFluidTypeExtensions.of(stack.fluidType)
        val stillTexture = fluidRenderProperties.stillTexture
        val sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture)
        val fluidColor = fluidRenderProperties.getTintColor(stack)

        val red = (fluidColor shr 16 and 255).toFloat() / 255.0f
        val green = (fluidColor shr 8 and 255).toFloat() / 255.0f
        val blue = (fluidColor and 255).toFloat() / 255.0f
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS)

        val pose = guiGraphics.pose()
        pose.pushPose()
        RenderSystem.setShaderColor(red, green, blue, 1.0f)

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
        fun build(stack: ItemStack, ctx: Void) = DiskResourceFluidRenderer(stack)
    }
}