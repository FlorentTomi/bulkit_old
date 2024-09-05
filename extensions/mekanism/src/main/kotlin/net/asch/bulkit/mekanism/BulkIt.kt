package net.asch.bulkit.mekanism

import mekanism.api.MekanismAPI
import mekanism.api.chemical.gas.Gas
import mekanism.common.capabilities.Capabilities
import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.BulkItExtension
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.mekanism.client.capability.ClientCapabilities
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.capability.drive_network.DriveNetworkViewGasHandler
import net.asch.bulkit.mekanism.common.command.Commands
import net.asch.bulkit.mekanism.common.command.DiskCommands
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.DeferredHolder
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@BulkItExtension.Name("mekanism")
@Mod("${BulkItApi.ID}_mekanism")
object BulkIt : BulkItExtension(MOD_BUS) {
    const val ID = "${BulkItApi.ID}_mekanism"

    val GAS_NON_RADIOACTIVE: DeferredHolder<ResourceType<*>, ResourceType<Gas>> =
        BulkIt.registerResourceType("gas_non_radioactive") {
            it.registry(MekanismAPI.GAS_REGISTRY).defaultDisk()
                .diskHandler(Capabilities.GAS.item, DiskGasHandler::buildOnlyNonRadioactive)
                .driveNetworkViewHandler(Capabilities.GAS.block, DriveNetworkViewGasHandler::buildOnlyNonRadioactive)
        }

    val GAS_RADIOACTIVE: DeferredHolder<ResourceType<*>, ResourceType<Gas>> =
        BulkIt.registerResourceType("gas_radioactive") {
            it.registry(MekanismAPI.GAS_REGISTRY).defaultDisk()
                .diskHandler(Capabilities.GAS.item, DiskGasHandler::buildOnlyRadioactive)
                .driveNetworkViewHandler(Capabilities.GAS.block, DriveNetworkViewGasHandler::buildOnlyRadioactive)
        }

    init {
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent::class.java, ::onRegisterCommands)
    }

    override fun initializeBoth(modBus: IEventBus) {
        super.initializeBoth(modBus)
        Commands.register(modBus)
    }

    @OnlyIn(Dist.CLIENT)
    override fun initializeClient(modBus: IEventBus) {
        modBus.addListener(
            RegisterCapabilitiesEvent::class.java, ClientCapabilities::register
        )
    }

    private fun onRegisterCommands(event: RegisterCommandsEvent) {
        DiskCommands.register(event.dispatcher, event.buildContext)
    }

    enum class GasFilter {
        ALL, ONLY_NON_RADIOACTIVE, ONLY_RADIOACTIVE
    }

    fun gasResource(filter: GasFilter) = when (filter) {
        GasFilter.ALL -> TODO()
        GasFilter.ONLY_NON_RADIOACTIVE -> GAS_NON_RADIOACTIVE
        GasFilter.ONLY_RADIOACTIVE -> GAS_RADIOACTIVE
    }
}