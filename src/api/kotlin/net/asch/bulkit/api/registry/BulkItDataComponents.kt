package net.asch.bulkit.api.registry

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.api.ResourceIdentifier
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object BulkItDataComponents {
    private val register = DeferredRegister.createDataComponents(BulkIt.ID)

    val RESOURCE_ITEM = registerResource<Item>("item", BuiltInRegistries.ITEM)
    val RESOURCE_FLUID = registerResource<Fluid>("fluid", BuiltInRegistries.FLUID)

    fun register(eventBus: IEventBus) = register.register(eventBus)

    fun <R> registerResource(key: String, resourceRegistry: Registry<R>) = register.registerComponentType("${key}_id") {
        BulkIt.logInfo("Registering ${resourceRegistry.key().registry()} resource")
        DataComponentType.builder<ResourceIdentifier<R>>().persistent(ResourceIdentifier.codec(resourceRegistry))
            .networkSynchronized(ResourceIdentifier.streamCodec(resourceRegistry)).cacheEncoding()
    }
}