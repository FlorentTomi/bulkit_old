package net.asch.bulkit.common.item

import net.asch.bulkit.BulkIt
import net.asch.bulkit.common.item.mod.CapacityDowngradeMod
import net.asch.bulkit.common.item.mod.CapacityUpgradeMod
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
}