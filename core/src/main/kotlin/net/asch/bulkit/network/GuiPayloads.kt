package net.asch.bulkit.network

import net.asch.bulkit.api.network.GuiPayloads
import net.asch.bulkit.common.menu.DiskMenu
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object GuiPayloads {
    fun register(registrar: PayloadRegistrar) {
        registrar.playToServer(
            GuiPayloads.Disk.TYPE, GuiPayloads.Disk.STREAM_CODEC, ::handlePayload
        )
    }

    private inline fun <reified PayloadType : CustomPacketPayload> handlePayload(
        payload: PayloadType, context: IPayloadContext
    ) {
        val player = context.player()
        when (payload.type()) {
            GuiPayloads.Disk.TYPE -> {
                player.openMenu(openDiskMenu())
            }
        }
    }

    private fun openDiskMenu(): MenuProvider = SimpleMenuProvider(
        { containerId, playerInventory, _ -> DiskMenu(containerId, playerInventory) }, Component.empty()
    )
}