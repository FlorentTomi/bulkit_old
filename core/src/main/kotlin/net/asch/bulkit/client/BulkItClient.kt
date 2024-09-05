package net.asch.bulkit.client

import net.asch.bulkit.BulkIt
import net.asch.bulkit.client.capability.ClientCapabilities
import net.asch.bulkit.client.command.DiskCommands
import net.asch.bulkit.client.gui.screen.MenuScreens
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.common.NeoForge
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkIt.ID, dist = [Dist.CLIENT])
object BulkItClient {
    init {
        val eventBus = MOD_BUS
        eventBus.addListener(
            RegisterCapabilitiesEvent::class.java, ClientCapabilities::register
        )
        eventBus.addListener(RegisterMenuScreensEvent::class.java, MenuScreens::register)

        NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent::class.java, ::onRegisterCommands)
    }

    private fun onRegisterCommands(event: RegisterClientCommandsEvent) {
        DiskCommands.register(event.dispatcher, event.buildContext)
    }
}