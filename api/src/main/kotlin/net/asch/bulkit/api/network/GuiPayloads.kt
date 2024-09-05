package net.asch.bulkit.api.network

import net.asch.bulkit.api.BulkItApi
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

object GuiPayloads {
    class Disk : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
            return TYPE
        }

        companion object {
            val INSTANCE: Disk = Disk()
            val TYPE: CustomPacketPayload.Type<Disk> = CustomPacketPayload.Type(BulkItApi.location("disk_gui"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Disk> = StreamCodec.unit(INSTANCE)
        }
    }
}