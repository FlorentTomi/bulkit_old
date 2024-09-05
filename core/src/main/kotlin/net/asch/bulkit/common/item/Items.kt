package net.asch.bulkit.common.item

import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.common.block.Blocks
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object Items {
    val REGISTER: DeferredRegister.Items = DeferredRegister.createItems(BulkItApi.ID)

    val DRIVE_NETWORK_CONFIGURATOR: DeferredItem<DriveNetworkConfigurator> =
        REGISTER.registerItem("drive_network_configurator") { props -> DriveNetworkConfigurator(props) }

    val CAPACITY_DOWNGRADE_MOD: DeferredItem<CapacityDowngradeMod> =
        REGISTER.registerItem("capacity_downgrade") { CapacityDowngradeMod() }
    val CAPACITY_UPGRADE_MODS = CapacityUpgradeMod.MULTIPLIERS.associateWith { multiplier ->
        REGISTER.registerItem("capacity_upgrade_$multiplier") {
            CapacityUpgradeMod(multiplier)
        }
    }

    fun register(eventBus: IEventBus) {
        Blocks.registerBlockItems(REGISTER)
        REGISTER.register(eventBus)
    }

    @Suppress("UNUSED_PARAMETER")
    fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
        output.accept(DRIVE_NETWORK_CONFIGURATOR)
        output.accept(CAPACITY_DOWNGRADE_MOD)
        CAPACITY_UPGRADE_MODS.forEach { output.accept(it.value) }
    }
}