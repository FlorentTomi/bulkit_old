package net.asch.bulkit.common.item

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object Items {
    val REGISTER: DeferredRegister.Items = DeferredRegister.createItems(BulkIt.ID)

    val CAPACITY_DOWNGRADE_MOD: DeferredItem<CapacityDowngradeMod> =
        REGISTER.registerItem("capacity_downgrade") { CapacityDowngradeMod() }
    val CAPACITY_UPGRADE_MODS = CapacityUpgradeMod.MULTIPLIERS.associateWith { multiplier ->
        REGISTER.registerItem("capacity_upgrade_$multiplier") {
            CapacityUpgradeMod(multiplier)
        }
    }

    fun register(eventBus: IEventBus) = REGISTER.register(eventBus)

    @Suppress("UNUSED_PARAMETER")
    fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
        output.accept(CAPACITY_DOWNGRADE_MOD)
        CAPACITY_UPGRADE_MODS.forEach { (_, mod) -> output.accept(mod) }
    }
}