package net.asch.bulkit.api.capability;

import net.asch.bulkit.api.BulkIt;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.items.IItemHandler;

public class Capabilities {
    public static class Disk {
        public static ItemCapability<IDiskResourceHandler, Void> RESOURCE = ItemCapability.createVoid(BulkIt.location("disk_resource"), IDiskResourceHandler.class);
        public static ItemCapability<IItemHandler, Void> MODS = ItemCapability.createVoid(BulkIt.location("disk_mods"), IItemHandler.class);
    }

    public static class DriveNetwork {
        public static BlockCapability<IDriveNetworkLink, Direction> LINK = BlockCapability.createSided(BulkIt.location("drive_network_link"), IDriveNetworkLink.class);
    }
}
