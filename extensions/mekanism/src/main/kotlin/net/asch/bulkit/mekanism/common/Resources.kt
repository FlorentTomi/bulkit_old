package net.asch.bulkit.mekanism.common

import mekanism.api.MekanismAPI
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.IGasHandler
import mekanism.common.capabilities.Capabilities
import net.asch.bulkit.api.registry.DeferredResources
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.mekanism.BulkItMekanism
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.asch.bulkit.mekanism.common.capability.drive_network.DriveNetworkViewGasHandler
import net.minecraft.core.Direction
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder

object Resources {
    private val REGISTER: DeferredResources = DeferredResources(BulkItMekanism.ID)

    val GAS_NON_RADIOACTIVE: DeferredHolder<ResourceType<*, *, *, *>, ResourceType<Gas, IGasHandler, IGasHandler, Direction>> =
        REGISTER.registerResourceType(ResourceType.Builder<Gas, IGasHandler, IGasHandler, Direction>(
            "gas_non_radioactive", BulkItMekanism.DATA_COMPONENTS, BulkItMekanism.ITEMS
        ).registry(MekanismAPI.GAS_REGISTRY).defaultDisk()
            .diskHandler(Capabilities.GAS.item) { stack, _ -> DiskGasHandler.buildOnlyNonRadioactive(stack) }
            .driveNetworkViewHandler(Capabilities.GAS.block, DriveNetworkViewGasHandler::buildOnlyNonRadioactive))

    val GAS_RADIOACTIVE: DeferredHolder<ResourceType<*, *, *, *>, ResourceType<Gas, IGasHandler, IGasHandler, Direction>> =
        REGISTER.registerResourceType(ResourceType.Builder<Gas, IGasHandler, IGasHandler, Direction>(
            "gas_radioactive", BulkItMekanism.DATA_COMPONENTS, BulkItMekanism.ITEMS
        ).registry(MekanismAPI.GAS_REGISTRY).defaultDisk()
            .diskHandler(Capabilities.GAS.item) { stack, _ -> DiskGasHandler.buildOnlyRadioactive(stack) }
            .driveNetworkViewHandler(Capabilities.GAS.block, DriveNetworkViewGasHandler::buildOnlyRadioactive))

    fun register(event: IEventBus) {
        REGISTER.register(event)
    }

    @Suppress("UNUSED_PARAMETER")
    fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
        output.accept(GAS_NON_RADIOACTIVE.get().disk)
        output.accept(GAS_RADIOACTIVE.get().disk)
    }
}