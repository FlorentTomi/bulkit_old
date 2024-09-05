package net.asch.bulkit.network

import net.asch.bulkit.api.BulkItApi
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

object Payloads {
    private const val VERSION = "1"

    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(BulkItApi.ID).versioned(VERSION)
        DiskPayloads.register(registrar)
        DriveNetworkPayloads.register(registrar)
        GuiPayloads.register(registrar)
    }
}