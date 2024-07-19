package net.asch.bulkit.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ResourceIdentifier<R>(Holder<R> resource, DataComponentPatch dataComponents) {
    public static <R> Codec<ResourceIdentifier<R>> codec(Registry<R> registry) {
        return RecordCodecBuilder.create((builder) -> builder.group(registry.holderByNameCodec().fieldOf("id").forGetter(ResourceIdentifier<R>::resource), DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ResourceIdentifier<R>::dataComponents)).apply(builder, ResourceIdentifier::new));
    }

    public static <R> StreamCodec<RegistryFriendlyByteBuf, ResourceIdentifier<R>> streamCodec(Registry<R> registry) {
        return StreamCodec.composite(ByteBufCodecs.holderRegistry(registry.key()), ResourceIdentifier<R>::resource, DataComponentPatch.STREAM_CODEC, ResourceIdentifier::dataComponents, ResourceIdentifier::new);
    }
}
