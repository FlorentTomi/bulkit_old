package net.asch.bulkit.common.menu

import net.asch.bulkit.api.BulkItApi
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object Menus {
    private val REGISTER = DeferredRegister.create(BuiltInRegistries.MENU, BulkItApi.ID)

    val DISK = REGISTER.register("disk") { -> MenuType(::DiskMenu, FeatureFlags.DEFAULT_FLAGS) }
    val DISK_DRIVE = REGISTER.register("disk_drive") { -> MenuType(::DiskDriveMenu, FeatureFlags.DEFAULT_FLAGS) }

    fun register(eventBus: IEventBus) = REGISTER.register(eventBus)
}