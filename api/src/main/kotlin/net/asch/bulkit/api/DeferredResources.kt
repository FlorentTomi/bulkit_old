package net.asch.bulkit.api

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegistryBuilder

class DeferredResources(namespace: String) : DeferredRegister<ResourceType<*, *, *, *>>(REGISTRY_KEY, namespace) {
    fun <T, DH, BH, BC> registerResourceType(
        builder: ResourceType.Builder<T, DH, BH, BC>
    ): DeferredHolder<ResourceType<*, *, *, *>, ResourceType<T, DH, BH, BC>> {
        BulkIt.logDebug("registering ${builder.key} in $REGISTRY_KEY")
        return register(builder.key, builder)
    }

    companion object {
        private val REGISTRY_KEY: ResourceKey<Registry<ResourceType<*, *, *, *>>> =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(BulkIt.ID, "resource_types"))
        val REGISTRY: Registry<ResourceType<*, *, *, *>> = RegistryBuilder(REGISTRY_KEY).sync(true).create()

        fun register(event: NewRegistryEvent) {
            event.register(REGISTRY)
        }
    }
}