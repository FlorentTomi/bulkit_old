package net.asch.bulkit.mekanism.common

import mekanism.api.MekanismAPI
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.IGasHandler
import mekanism.common.capabilities.Capabilities
import net.asch.bulkit.api.DeferredResources
import net.asch.bulkit.api.ResourceType
import net.asch.bulkit.mekanism.BulkItMekanism
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.capability.drive_network.DriveNetworkViewGasHandler
import net.minecraft.core.Direction
import net.neoforged.bus.api.IEventBus

object Resources {
    private val REGISTER: DeferredResources = DeferredResources(BulkItMekanism.ID)

    val GAS_NON_RADIOACTIVE = REGISTER.registerResourceType(
        ResourceType.Builder<Gas, IGasHandler, IGasHandler, Direction?>(
            "gas_non_radioactive", BulkItMekanism.DATA_COMPONENTS, BulkItMekanism.ITEMS
        ).registry(MekanismAPI.GAS_REGISTRY).defaultDisk()
            .diskHandler(Capabilities.GAS.item, DiskGasHandler::buildOnlyNonRadioactive)
            .driveNetworkViewHandler(Capabilities.GAS.block, DriveNetworkViewGasHandler::buildOnlyNonRadioactive)
    )

    val GAS_RADIOACTIVE = REGISTER.registerResourceType(
        ResourceType.Builder<Gas, IGasHandler, IGasHandler, Direction?>(
            "gas_radioactive", BulkItMekanism.DATA_COMPONENTS, BulkItMekanism.ITEMS
        ).registry(MekanismAPI.GAS_REGISTRY).defaultDisk()
            .diskHandler(Capabilities.GAS.item, DiskGasHandler::buildOnlyRadioactive)
            .driveNetworkViewHandler(Capabilities.GAS.block, DriveNetworkViewGasHandler::buildOnlyRadioactive)
    )

    fun register(event: IEventBus) {
        REGISTER.register(event)
    }
}