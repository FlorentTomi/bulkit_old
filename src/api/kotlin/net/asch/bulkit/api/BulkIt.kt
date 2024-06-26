package net.asch.bulkit.api

import org.apache.logging.log4j.LogManager

object BulkIt {
    const val ID = "bulkit"
    private val LOGGER = LogManager.getLogger()

    fun extensionId(extensionName: String) = "${ID}_$extensionName"
    fun logInfo(msg: String) = LOGGER.info("[$ID]: $msg")
}