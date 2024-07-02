package net.asch.bulkit.common.network

import net.asch.bulkit.BulkIt
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

object Payloads {
    private const val VERSION = "1"

    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(BulkIt.ID).versioned(VERSION)
        DiskPayloads.register(registrar)
    }
}