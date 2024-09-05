package net.asch.bulkit.api

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.PacketDistributor

object BulkItApi {
    const val ID: String = "bulkit"

    fun location(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(ID, path)

    fun <PayloadType : CustomPacketPayload> sendToServer(payload: PayloadType) {
        PacketDistributor.sendToServer(payload)
    }
}