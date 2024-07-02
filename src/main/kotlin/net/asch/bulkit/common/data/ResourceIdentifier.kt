package net.asch.bulkit.common.data

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
        fun <R> codec(registry: Registry<R>): Codec<ResourceIdentifier<R>> = RecordCodecBuilder.create { builder ->
            builder.group(
                registry.holderByNameCodec().fieldOf("id").forGetter(ResourceIdentifier<R>::resource),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                    .forGetter(ResourceIdentifier<R>::dataComponents)
            ).apply(builder, ::ResourceIdentifier)
        }

        fun <R> streamCodec(registry: Registry<R>): StreamCodec<RegistryFriendlyByteBuf, ResourceIdentifier<R>> =
            StreamCodec.composite(
                ByteBufCodecs.holderRegistry(registry.key()),
                ResourceIdentifier<R>::resource,
                DataComponentPatch.STREAM_CODEC,
                ResourceIdentifier<R>::dataComponents,
                ::ResourceIdentifier
            )
    }
}
