package net.asch.bulkit.mekanism

import mekanism.api.MekanismAPI
import mekanism.api.chemical.gas.Gas
import mekanism.common.capabilities.Capabilities
import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.ResourceHolder
import net.asch.bulkit.mekanism.common.capability.disk.DiskContentGasHandler
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.capability.drive_network.DriveNetworkViewGasHandler
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkItMekanism.ID)
object BulkItMekanism {
    const val ID = "${BulkIt.ID}_mekanism"

    private val DATA_COMPONENTS = DeferredRegister.createDataComponents(ID)
    private val ITEMS = DeferredRegister.createItems(ID)

    val RESOURCE_GAS_NON_RADIOACTIVE = ResourceHolder.Builder<Gas>(DATA_COMPONENTS, ITEMS).build(
        "gas_non_radioactive",
        MekanismAPI.GAS_REGISTRY,
        Capabilities.GAS.item,
        DiskGasHandler::createOnlyNonRadioactive,
        DiskContentGasHandler::createOnlyNonRadioactive,
        Capabilities.GAS.block,
        DriveNetworkViewGasHandler::createOnlyNonRadioactive
    )

    val RESOURCE_GAS_RADIOACTIVE = ResourceHolder.Builder<Gas>(DATA_COMPONENTS, ITEMS).build(
        "gas_non_radioactive",
        MekanismAPI.GAS_REGISTRY,
        Capabilities.GAS.item,
        DiskGasHandler::createOnlyRadioactive,
        DiskContentGasHandler::createOnlyRadioactive,
        Capabilities.GAS.block,
        DriveNetworkViewGasHandler::createOnlyRadioactive
    )

    init {
        val eventBus = MOD_BUS
        eventBus.addListener(RegisterCapabilitiesEvent::class.java, ::registerCapabilities)
        register(eventBus)
    }

    private fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
        DATA_COMPONENTS.register(eventBus)
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        RESOURCE_GAS_NON_RADIOACTIVE.registerCapabilities(event)
        RESOURCE_GAS_RADIOACTIVE.registerCapabilities(event)
    }
}