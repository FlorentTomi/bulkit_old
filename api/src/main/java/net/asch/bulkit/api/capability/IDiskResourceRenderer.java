package net.asch.bulkit.api.capability;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface IDiskResourceRenderer {
    @NotNull Component getDescription();
    void render(@NotNull GuiGraphics guiGraphics, int x, int y, int size);
}
