package net.asch.bulkit.mekanism.network

import mekanism.api.Action
import mekanism.api.chemical.gas.GasStack
import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.mekanism.BulkIt
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object DiskPayloads {
    fun register(registrar: PayloadRegistrar) {
        registrar.playToServer(AddGas.TYPE, AddGas.STREAM_CODEC, ::handlePayload)
    }

    fun addGas(stack: GasStack) = BulkItApi.sendToServer(AddGas(stack))

    private inline fun <reified PayloadType : CustomPacketPayload> handlePayload(
        payload: PayloadType, context: IPayloadContext
    ) {
        val player = context.player()
        val mainHandItem = player.mainHandItem
        when (payload.type()) {
            AddGas.TYPE -> {
                val diskCapability = mainHandItem.getCapability(mekanism.common.capabilities.Capabilities.GAS.item)
                diskCapability?.insertChemical(0, (payload as AddGas).stack, Action.EXECUTE)
            }
        }
    }

    private class AddGas(val stack: GasStack) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<AddGas>(BulkIt.location("disk_add_gas"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AddGas> = StreamCodec.composite(
                GasStack.OPTIONAL_STREAM_CODEC, AddGas::stack, ::AddGas
            )
        }
    }
}