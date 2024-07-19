package net.asch.bulkit.api;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BulkIt {
    static public final String ID = "bulkit";
    static private final Logger LOGGER = LogManager.getLogger();

    static public ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }

    static public void logInfo(String msg) {
        LOGGER.info(msg);
    }

    static public void logDebug(String msg) {
        LOGGER.debug(msg);
    }
}
