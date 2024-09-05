package net.asch.bulkit.datagen

import net.asch.bulkit.api.BulkItApi
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class BlockTags(
    packOutput: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    helper: ExistingFileHelper
) : BlockTagsProvider(packOutput, lookupProvider, BulkItApi.ID, helper) {
    override fun addTags(provider: HolderLookup.Provider) {

    }
}