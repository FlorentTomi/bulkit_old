package net.asch.bulkit.mekanism

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.mekanism.common.Resources
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkItMekanism.ID)
object BulkItMekanism {
    const val ID = "${BulkIt.ID}_mekanism"

    val DATA_COMPONENTS: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(ID)
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(ID)

    init {
        BulkIt.logDebug("${BulkIt.ID} extension: mekanism")
        val eventBus = MOD_BUS
        eventBus.addListener(RegisterCapabilitiesEvent::class.java, ::registerCapabilities)
        register(eventBus)
    }

    private fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
        DATA_COMPONENTS.register(eventBus)
        Resources.register(eventBus)
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
    }

    enum class GasFilter {
        ALL, ONLY_NON_RADIOACTIVE, ONLY_RADIOACTIVE
    }

    fun gasResource(filter: GasFilter) = when (filter) {
        GasFilter.ALL -> TODO()
        GasFilter.ONLY_NON_RADIOACTIVE -> Resources.GAS_NON_RADIOACTIVE
        GasFilter.ONLY_RADIOACTIVE -> Resources.GAS_RADIOACTIVE
    }
}