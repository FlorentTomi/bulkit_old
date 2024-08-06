package net.asch.bulkit.api.capability;

import net.asch.bulkit.api.BulkIt;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class Capabilities {
    public static class Disk {
        public static ItemCapability<IDiskResourceHandler, @Nullable Void> RESOURCE = ItemCapability.createVoid(BulkIt.location("disk_resource"), IDiskResourceHandler.class);
        public static ItemCapability<IItemHandler, @Nullable Void> MODS = ItemCapability.createVoid(BulkIt.location("disk_mods"), IItemHandler.class);

        public static ItemCapability<IDiskResourceRenderer, @Nullable Void> RESOURCE_RENDERER = ItemCapability.createVoid(BulkIt.location("disk_resource_renderer"), IDiskResourceRenderer.class);
    }

    public static class DriveNetwork {
        public static BlockCapability<IDriveNetworkLink, @Nullable Direction> LINK = BlockCapability.createSided(BulkIt.location("drive_network_link"), IDriveNetworkLink.class);
    }
}
