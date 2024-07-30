package net.asch.bulkit.datagen

import net.asch.bulkit.api.BulkIt
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(modid = BulkIt.ID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    private fun onGatherData(event: GatherDataEvent) {
        val generator = event.generator

        val blockTags = BlockTags(generator.packOutput, event.lookupProvider, event.existingFileHelper)
        generator.addProvider(event.includeServer(), blockTags)

        val itemTags =
            ItemTags(generator.packOutput, event.lookupProvider, blockTags.contentsGetter(), event.existingFileHelper)
        generator.addProvider(event.includeServer(), itemTags)
    }
}