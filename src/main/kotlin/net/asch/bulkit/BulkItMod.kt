package net.asch.bulkit

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.registry.BulkItDataComponents
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkIt.ID)
object BulkItMod {
    init {
        val eventBus = MOD_BUS
        register(eventBus)
    }

    private fun register(eventBus: IEventBus) {
        BulkItDataComponents.register(eventBus)
    }
}