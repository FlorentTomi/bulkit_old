package net.asch.bulkit.network

import io.netty.buffer.ByteBuf
import net.asch.bulkit.BulkIt
import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.capability.Capabilities
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object DriveNetworkPayloads {
    fun register(registrar: PayloadRegistrar) {
        registrar.playToServer(Link.TYPE, Link.STREAM_CODEC, ::handlePayload)
    }

    fun link(pos: BlockPos, rootPos: BlockPos?) = BulkItApi.sendToServer(Link(pos, rootPos))

    private inline fun <reified PayloadType : CustomPacketPayload> handlePayload(
        payload: PayloadType, context: IPayloadContext
    ) {
        val player = context.player()
        val level = player.level()
        when (payload.type()) {
            Link.TYPE -> {
                val linkPayload = payload as Link
                level.getCapability(Capabilities.DriveNetwork.LINK, linkPayload.pos, player.direction)
                    ?.linkTo(player, linkPayload.rootPos)
            }
        }
    }

    private class Link(val pos: BlockPos, val rootPos: BlockPos?) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<Link>(BulkIt.location("drive_network_link"))
            val STREAM_CODEC: StreamCodec<ByteBuf, Link> = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Link::pos, BlockPos.STREAM_CODEC, Link::rootPos, ::Link
            )
        }
    }
}