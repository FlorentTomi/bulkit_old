package net.asch.bulkit.datagen

import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.common.item.Items
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ItemTags(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    blockTagProvider: CompletableFuture<TagLookup<Block>>,
    helper: ExistingFileHelper
) : ItemTagsProvider(packOutput, lookupProvider, blockTagProvider, BulkIt.ID, helper) {
    override fun addTags(provider: HolderLookup.Provider) {
        tag(WRENCHES).add(Items.DRIVE_NETWORK_CONFIGURATOR.get())
        tag(TOOLS_WRENCH).add(Items.DRIVE_NETWORK_CONFIGURATOR.get())
    }

    companion object {
        private val WRENCHES: TagKey<Item> = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "wrenches"))
        private val TOOLS_WRENCH: TagKey<Item> =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"))
    }
}