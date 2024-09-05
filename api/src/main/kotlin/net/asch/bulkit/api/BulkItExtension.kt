package net.asch.bulkit.api

import net.asch.bulkit.api.registry.DeferredResources
import net.asch.bulkit.api.registry.ResourceType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.UnaryOperator

abstract class BulkItExtension protected constructor(modBus: IEventBus) {
    val id: String
    private val logger: Logger = LogManager.getLogger()

    val dataComponents: DeferredRegister.DataComponents
    val items: DeferredRegister.Items
    private val resources: DeferredResources
    val creativeTabs: DeferredRegister<CreativeModeTab>

    init {
        val thisClass: Class<*> = this.javaClass
        if (!thisClass.isAnnotationPresent(Name::class.java)) {
            throw RuntimeException("Extension does not have a name")
        }

        val extensionName = thisClass.getDeclaredAnnotation(Name::class.java)
        this.id = BulkItApi.ID + "_" + extensionName.value
        logger.debug(BulkItApi.ID + " extension: {}", extensionName.value)

        this.dataComponents = DeferredRegister.createDataComponents(id)
        this.items = DeferredRegister.createItems(id)
        this.resources = DeferredResources.create(id)
        this.creativeTabs = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, id)

        this.initializeBoth(modBus)
        if (FMLEnvironment.dist == Dist.CLIENT) {
            this.initializeClient(modBus)
        }
    }

    fun location(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(id, path)
    fun log(msg: Any) = logger.info(msg)

    fun <T> registerResourceType(
        name: String, builderOp: UnaryOperator<ResourceType.Builder<T>>
    ): DeferredHolder<ResourceType<*>, ResourceType<T>> {
        val builder = ResourceType.Builder<T>(name, this.dataComponents, this.items)
        builderOp.apply(builder)
        return resources.registerResourceType(builder)
    }

    protected open fun initializeBoth(modBus: IEventBus) {
        dataComponents.register(modBus)
        items.register(modBus)
        resources.register(modBus)
        creativeTabs.register(modBus)
    }

    protected open fun initializeClient(modBus: IEventBus) {
    }

    annotation class Name(val value: String)
}