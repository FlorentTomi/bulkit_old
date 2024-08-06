package net.asch.bulkit.mekanism

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.mekanism.common.Resources
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredRegister
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(BulkItMekanism.ID)
object BulkItMekanism {
    const val ID = "${BulkIt.ID}_mekanism"
    private val LOGGER: Logger = LogManager.getLogger()

    val DATA_COMPONENTS: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(ID)
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(ID)

    private val CREATIVE_TAB_REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, ID)
    private val CREATIVE_TAB = CREATIVE_TAB_REGISTER.register(ID) { ->
        CreativeModeTab.builder().title(Component.literal("BulkIt (Mekanism)")).displayItems(::registerToCreativeTab)
            .build()
    }

    init {
        LOGGER.debug("${BulkIt.ID} extension: mekanism")
        val eventBus = MOD_BUS
        register(eventBus)
    }

    private fun register(eventBus: IEventBus) {
        CREATIVE_TAB_REGISTER.register(eventBus)
        ITEMS.register(eventBus)
        DATA_COMPONENTS.register(eventBus)
        Resources.register(eventBus)
    }

    private fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
    }

    enum class GasFilter {
        ALL, ONLY_NON_RADIOACTIVE, ONLY_RADIOACTIVE
    }

    fun gasResource(filter: GasFilter) = when (filter) {
        GasFilter.ALL -> TODO()
        GasFilter.ONLY_NON_RADIOACTIVE -> Resources.GAS_NON_RADIOACTIVE
        GasFilter.ONLY_RADIOACTIVE -> Resources.GAS_RADIOACTIVE
    }
}