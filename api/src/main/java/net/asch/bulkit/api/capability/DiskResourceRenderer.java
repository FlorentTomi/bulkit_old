package net.asch.bulkit.api.capability;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.asch.bulkit.api.data.ResourceIdentifier;
import net.asch.bulkit.api.registry.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Objects;

public abstract class DiskResourceRenderer<T> implements IDiskResourceRenderer {
    private static final ItemStack EMPTY_ITEM = Items.BARRIER.getDefaultInstance();
    private final ItemStack disk;
    private final DataComponentType<ResourceIdentifier<T>> resourceIdType;

    public DiskResourceRenderer(@NotNull ItemStack disk, @NotNull ResourceType<T> resourceType) {
        this.disk = disk;
        this.resourceIdType = resourceType.id.get();
    }

    @Override
    public @NotNull Component getDescription() {
        @Nullable ResourceIdentifier<T> resourceId = disk.get(resourceIdType);
        if (resourceId == null) {
            return Component.empty();
        }

        return getResourceDescription(resourceId);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, int size) {
        @Nullable ResourceIdentifier<T> resourceId = disk.get(resourceIdType);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0f);

        if (resourceId == null) {
            renderItem(EMPTY_ITEM, guiGraphics, size);
            return;
        }

        IDiskResourceHandler diskResource = Objects.requireNonNull(disk.getCapability(Capabilities.Disk.RESOURCE));
        renderResource(resourceId, diskResource.getAmount(), guiGraphics, size);

        guiGraphics.pose().popPose();
    }

    protected abstract Component getResourceDescription(@NotNull ResourceIdentifier<T> resourceId);
    protected abstract void renderResource(@NotNull ResourceIdentifier<T> resourceId, long amount, @NotNull GuiGraphics guiGraphics, int size);

    protected void renderItem(@NotNull ItemStack stack, @NotNull GuiGraphics guiGraphics, int size) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel itemModel = itemRenderer.getModel(stack, Minecraft.getInstance().level, null, 0);

        PoseStack pose = guiGraphics.pose();
        pose.translate(0.5f * size, 0.5f * size, 150.0f);
        pose.mulPose(new Matrix4f().scale(1.0f, -1.0f, 1.0f));
        pose.scale(size - 2.0f, size - 2.0f, size - 2.0f);

        if (!itemModel.usesBlockLight()) {
            Lighting.setupForFlatItems();
        }

        itemRenderer.render(stack, ItemDisplayContext.GUI, false, pose, guiGraphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, itemModel);

        guiGraphics.flush();
        if (!itemModel.usesBlockLight()) {
            Lighting.setupFor3DItems();
        }
    }
}
