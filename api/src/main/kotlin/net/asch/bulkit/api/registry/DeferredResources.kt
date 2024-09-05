package net.asch.bulkit.api.registry

import net.asch.bulkit.api.BulkItApi
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegistryBuilder

class DeferredResources(namespace: String) : DeferredRegister<ResourceType<*>>(REGISTRY_KEY, namespace) {
    fun <T> registerResourceType(builder: ResourceType.Builder<T>): DeferredHolder<ResourceType<*>, ResourceType<T>> =
        register(builder.key, builder)

    companion object {
        private val REGISTRY_KEY: ResourceKey<Registry<ResourceType<*>>> =
            ResourceKey.createRegistryKey(BulkItApi.location("resource_types"))
        val REGISTRY: Registry<ResourceType<*>> = RegistryBuilder(REGISTRY_KEY).sync(true).create()

        fun create(namespace: String): DeferredResources = DeferredResources(namespace)
        fun register(event: NewRegistryEvent) = event.register(REGISTRY)
        fun registeredResources(): Collection<ResourceType<*>> = REGISTRY.stream().toList()
    }
}