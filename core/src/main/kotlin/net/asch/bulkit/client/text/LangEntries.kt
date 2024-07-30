package net.asch.bulkit.client.text

import net.asch.bulkit.api.BulkIt
import net.minecraft.network.chat.Component

enum class LangEntries(vararg path: String) {
    CONFIGURATOR_CLEAR_ROOT("configurator", "clear_root"),
    CONFIGURATOR_SET_ROOT("configurator", "set_root"),
    CONFIGURATOR_LINK("configurator", "link")
    ;

    private val translationKey = "${BulkIt.ID}.${path.joinToString(".")}"

    fun component(): Component = Component.translatable(translationKey)
    fun <T> component(vararg keys: T): Component = Component.translatable(translationKey, *keys)
}