package net.asch.bulkit.client.gui.screen

import net.asch.bulkit.common.menu.Menus
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

object MenuScreens {
    fun register(event: RegisterMenuScreensEvent) {
        event.register(Menus.DISK.get(), ::DiskScreen)
    }
}