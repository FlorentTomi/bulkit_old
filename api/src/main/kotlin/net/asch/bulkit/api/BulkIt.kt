package net.asch.bulkit.api

import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager

object BulkIt {
    const val ID = "bulkit"
    private val LOGGER = LogManager.getLogger()

    fun location(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(ID, path)
    fun logInfo(msg: String) = LOGGER.info(msg)
    fun logDebug(msg: String) = LOGGER.debug(msg)
}