package net.asch.bulkit.api.capability

import com.mojang.blaze3d.platform.Lighting
import net.asch.bulkit.api.data.ResourceIdentifier
import net.asch.bulkit.api.registry.ResourceType
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix4f
import java.util.*

abstract class DiskResourceRenderer<T>(private val disk: ItemStack, resourceType: ResourceType<T>) :
    IDiskResourceRenderer {
    private val resourceIdType = resourceType.id.get()

    override val capacity: Long
        get() {
            val id = disk.get(resourceIdType) ?: return 0
            val handler = disk.getCapability(Capabilities.Disk.RESOURCE) ?: return 0
            return getResourceCapacity(id, handler)
        }

    override val description: Component
        get() {
            val resourceId = disk.get(resourceIdType) ?: return Component.empty()
            return getResourceDescription(resourceId)
        }

    override fun render(guiGraphics: GuiGraphics, x: Int, y: Int, size: Int) {
        val resourceId = disk.get(resourceIdType)

        guiGraphics.pose().pushPose()
        guiGraphics.pose().translate(x.toFloat(), y.toFloat(), 0.0f)

        if (resourceId == null) {
            renderItem(EMPTY_ITEM, guiGraphics, size)
            return
        }

        val diskResource = Objects.requireNonNull(disk.getCapability(Capabilities.Disk.RESOURCE))
        renderResource(resourceId, diskResource!!, guiGraphics, size)

        guiGraphics.pose().popPose()
    }

    protected abstract fun getResourceCapacity(id: ResourceIdentifier<T>, handler: IDiskResourceHandler): Long
    protected abstract fun getResourceDescription(id: ResourceIdentifier<T>): Component
    protected abstract fun renderResource(
        id: ResourceIdentifier<T>, handler: IDiskResourceHandler, guiGraphics: GuiGraphics, size: Int
    )

    protected fun renderItem(stack: ItemStack, guiGraphics: GuiGraphics, size: Int) {
        val itemRenderer = Minecraft.getInstance().itemRenderer
        val itemModel = itemRenderer.getModel(stack, Minecraft.getInstance().level, null, 0)

        val pose = guiGraphics.pose()
        pose.translate(0.5f * size, 0.5f * size, 150.0f)
        pose.mulPose(Matrix4f().scale(1.0f, -1.0f, 1.0f))
        pose.scale(size - 2.0f, size - 2.0f, size - 2.0f)

        if (!itemModel.usesBlockLight()) {
            Lighting.setupForFlatItems()
        }

        itemRenderer.render(
            stack,
            ItemDisplayContext.GUI,
            false,
            pose,
            guiGraphics.bufferSource(),
            15728880,
            OverlayTexture.NO_OVERLAY,
            itemModel
        )

        guiGraphics.flush()
        if (!itemModel.usesBlockLight()) {
            Lighting.setupFor3DItems()
        }
    }

    companion object {
        private val EMPTY_ITEM = Items.BARRIER.defaultInstance
    }
}