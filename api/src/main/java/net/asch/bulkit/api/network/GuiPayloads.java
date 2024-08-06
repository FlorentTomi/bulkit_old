package net.asch.bulkit.api.network;

import net.asch.bulkit.api.BulkIt;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class GuiPayloads {
    public record Disk() implements CustomPacketPayload {
        public static final Disk INSTANCE = new Disk();
        public static final Type<Disk> TYPE = new Type<>(BulkIt.location("disk_gui"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Disk> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
