package net.asch.bulkit.api.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class ResourceIdentifier<R>(val resource: Holder<R>, val dataComponents: DataComponentPatch) {
    companion object {
        private val RESOURCELESS_INSTANCE = ResourceIdentifier(Holder.direct(Unit), DataComponentPatch.EMPTY)
        val RESOURCELESS_CODEC = Codec.unit(RESOURCELESS_INSTANCE)

        fun <R> codec(registry: Registry<R>): Codec<ResourceIdentifier<R>> {
            return RecordCodecBuilder.create { builder ->
                builder.group(
                    registry.holderByNameCodec().fieldOf("resource").forGetter(ResourceIdentifier<R>::resource),
                    DataComponentPatch.CODEC.optionalFieldOf(
                        "components", DataComponentPatch.EMPTY
                    ).forGetter(ResourceIdentifier<R>::dataComponents)
                ).apply(
                    builder
                ) { resource, dataComponents -> ResourceIdentifier(resource, dataComponents) }
            }
        }

        fun <R> streamCodec(registry: Registry<R>): StreamCodec<RegistryFriendlyByteBuf, ResourceIdentifier<R>> {
            return StreamCodec.composite(
                ByteBufCodecs.holderRegistry(registry.key()),
                ResourceIdentifier<R>::resource,
                DataComponentPatch.STREAM_CODEC,
                ResourceIdentifier<R>::dataComponents
            ) { resource, dataComponents ->
                ResourceIdentifier(
                    resource, dataComponents
                )
            }
        }
    }
}