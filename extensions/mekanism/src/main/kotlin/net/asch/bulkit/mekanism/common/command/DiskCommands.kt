package net.asch.bulkit.mekanism.common.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.LongArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.mekanism.BulkIt
import net.asch.bulkit.mekanism.common.command.gas.GasArgument
import net.asch.bulkit.mekanism.common.command.gas.GasInput
import net.asch.bulkit.mekanism.network.DiskPayloads
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack

object DiskCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, buildContext: CommandBuildContext) {
//        val builder = LiteralArgumentBuilder.literal<CommandSourceStack>("${BulkIt.ID}:disk")
//        thenAdd(builder, buildContext)
//        dispatcher.register(builder)
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenAdd(
        builder: ArgumentBuilder<CommandSourceStack, T>, buildContext: CommandBuildContext
    ) {
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("add").requires { it.hasPermission(2) }.then(
                LiteralArgumentBuilder.literal<CommandSourceStack>("gas")
                    .then(
                        RequiredArgumentBuilder.argument<CommandSourceStack, GasInput>(
                            "gas", GasArgument.gas(buildContext)
                        )
                            .then(
                                RequiredArgumentBuilder.argument<CommandSourceStack, Long>(
                                    "amount", LongArgumentType.longArg(0)
                                ).executes(::addGas)
                            )
                    )
            )
        )
    }

    private fun addGas(context: CommandContext<CommandSourceStack>): Int {
        val gas = GasArgument.gasOf(context, "gas")
        val amount = LongArgumentType.getLong(context, "amount")
        val stack = gas.createGasStack(amount)
        DiskPayloads.addGas(stack)
        return 0
    }
}