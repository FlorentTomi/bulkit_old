package net.asch.bulkit.client.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.ClientCapabilities
import net.minecraft.client.player.LocalPlayer
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component

object DiskCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, buildContext: CommandBuildContext) {
        val builder = LiteralArgumentBuilder.literal<CommandSourceStack>("${BulkItApi.ID}:disk")
        thenPrint(builder)
        dispatcher.register(builder)
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenPrint(builder: ArgumentBuilder<CommandSourceStack, T>) =
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("print").executes(::print)
        )

    private fun print(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.source as LocalPlayer

        val disk = player.mainHandItem
        val diskCap = disk.getCapability(Capabilities.Disk.RESOURCE) ?: return -1
        val resourceRendererCap = disk.getCapability(ClientCapabilities.RESOURCE_RENDERER) ?: return -1
        val msg = Component.empty().append(resourceRendererCap.description)
            .append(": ${diskCap.amountL} / ${resourceRendererCap.capacity}")

        player.displayClientMessage(msg, true)
        return 0
    }
}