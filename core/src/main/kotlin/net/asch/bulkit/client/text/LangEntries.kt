package net.asch.bulkit.client.text

import net.asch.bulkit.api.BulkItApi
import net.minecraft.network.chat.Component

enum class LangEntries(vararg path: String) {
    RESOURCE_ENERGY("resource", "energy", "name"),

    CONFIGURATOR_CLEAR_ROOT("configurator", "clear_root"),
    CONFIGURATOR_SET_ROOT("configurator", "set_root"),
    CONFIGURATOR_LINK("configurator", "link"),

    SCREEN_DISK_AMOUNT("screen", "disk", "amount"),
    SCREEN_DISK_LOCKED("screen", "disk", "locked"),
    SCREEN_DISK_VOID("screen", "disk", "void")
    ;

    private val translationKey = "${BulkItApi.ID}.${path.joinToString(".")}"

    fun component(): Component = Component.translatable(translationKey)
    fun <T> component(vararg keys: T): Component = Component.translatable(translationKey, *keys)
}