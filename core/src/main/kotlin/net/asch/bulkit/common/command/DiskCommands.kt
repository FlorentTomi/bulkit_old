package net.asch.bulkit.common.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.asch.bulkit.api.BulkIt
import net.asch.bulkit.network.DiskPayloads
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.commands.arguments.item.ItemInput

object DiskCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, buildContext: CommandBuildContext) {
        val builder = LiteralArgumentBuilder.literal<CommandSourceStack>("${BulkIt.ID}:disk")
        thenPrint(builder)
        thenAdd(builder, buildContext)
        thenGrow(builder)
        thenShrink(builder)
        thenLock(builder)
        thenVoid(builder)
        dispatcher.register(builder)
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenPrint(builder: ArgumentBuilder<CommandSourceStack, T>) =
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("print").executes(::print)
        )

    @Suppress("UNUSED_PARAMETER")
    private fun print(context: CommandContext<CommandSourceStack>): Int {
//        val disk = context.source.player?.mainHandItem ?: return -1
//        val diskCapability = disk.getCapability(Capabilities.DISK_RESOURCE) ?: return -1
//        val msg = Component.empty().append(diskCapability.description)
//            .append(": ${diskCapability.amount} / ${diskCapability.capacity}")

//        BulkIt.sendMessageTo(context.source.player!!, msg)
        return 0
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenLock(builder: ArgumentBuilder<CommandSourceStack, T>) =
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("lock").requires { it.hasPermission(2) }.then(
                RequiredArgumentBuilder.argument<CommandSourceStack, Boolean>("locked", BoolArgumentType.bool())
                    .executes(::lock)
            )
        )

    private fun lock(context: CommandContext<CommandSourceStack>): Int {
        val locked = BoolArgumentType.getBool(context, "locked")
        DiskPayloads.lock(locked)
        return 0
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenVoid(builder: ArgumentBuilder<CommandSourceStack, T>) =
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("void").requires { it.hasPermission(2) }.then(
                RequiredArgumentBuilder.argument<CommandSourceStack, Boolean>("void", BoolArgumentType.bool())
                    .executes(::void)
            )
        )

    private fun void(context: CommandContext<CommandSourceStack>): Int {
        val void = BoolArgumentType.getBool(context, "void")
        DiskPayloads.void(void)
        return 0
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenAdd(
        builder: ArgumentBuilder<CommandSourceStack, T>, buildContext: CommandBuildContext
    ) {
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("add").requires { it.hasPermission(2) }.then(
                LiteralArgumentBuilder.literal<CommandSourceStack>("item").then(
                    RequiredArgumentBuilder.argument<CommandSourceStack, ItemInput>(
                        "item", ItemArgument.item(buildContext)
                    ).then(
                        RequiredArgumentBuilder.argument<CommandSourceStack, Int>(
                            "amount", IntegerArgumentType.integer(0)
                        ).executes(::addItem)
                    )
                )
            )
//            .then(
//            LiteralArgumentBuilder.literal<CommandSourceStack>("fluid").then(
//                RequiredArgumentBuilder.argument<CommandSourceStack, FluidInput>(
//                    "fluid", FluidArgument.fluid(buildContext)
//                ).then(
//                    RequiredArgumentBuilder.argument<CommandSourceStack, Int>(
//                        "amount", IntegerArgumentType.integer(0)
//                    ).executes(::addFluid)
//                )
//            )
//        )
        )
    }

    private fun addItem(context: CommandContext<CommandSourceStack>): Int {
        val item = ItemArgument.getItem(context, "item")
        val amount = IntegerArgumentType.getInteger(context, "amount")
        val stack = item.createItemStack(amount, true)
        DiskPayloads.addItem(stack)
        return 0
    }

//    private fun addFluid(context: CommandContext<CommandSourceStack>): Int {
//        val fluid = FluidArgument.fluidOf(context, "fluid")
//        val amount = IntegerArgumentType.getInteger(context, "amount")
//        val stack = fluid.createFluidStack(amount)
//        DiskPayloads.addFluid(stack)
//        return 0
//    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenGrow(builder: ArgumentBuilder<CommandSourceStack, T>) =
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("grow").requires { it.hasPermission(2) }.then(
                RequiredArgumentBuilder.argument<CommandSourceStack, Int>("amount", IntegerArgumentType.integer(0))
                    .executes(::grow)
            )
        )

    private fun grow(context: CommandContext<CommandSourceStack>): Int {
        val amount = IntegerArgumentType.getInteger(context, "amount")
        DiskPayloads.grow(amount.toLong())
        return 0
    }

    private fun <T : ArgumentBuilder<CommandSourceStack, T>> thenShrink(builder: ArgumentBuilder<CommandSourceStack, T>) =
        builder.then(
            LiteralArgumentBuilder.literal<CommandSourceStack>("shrink").requires { it.hasPermission(2) }.then(
                RequiredArgumentBuilder.argument<CommandSourceStack, Int>("amount", IntegerArgumentType.integer(0))
                    .executes(::shrink)
            )
        )

    private fun shrink(context: CommandContext<CommandSourceStack>): Int {
        val amount = IntegerArgumentType.getInteger(context, "amount")
        DiskPayloads.shrink(amount.toLong())
        return 0
    }
}