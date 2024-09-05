package net.asch.bulkit.mekanism.common.command.gas

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandBuildContext
import java.util.concurrent.CompletableFuture

class GasArgument(context: CommandBuildContext) : ArgumentType<GasInput> {
    private val parser = GasParser(context)

    override fun parse(reader: StringReader): GasInput {
        val gasResult = parser.parse(reader)
        return GasInput(gasResult.gas)
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> = parser.fillSuggestions(builder)

    override fun getExamples(): Collection<String> = EXAMPLES

    companion object {
        private val EXAMPLES = listOf("oxygen", "mekanism:oxygen")

        fun gas(context: CommandBuildContext): GasArgument = GasArgument(context)
        fun <S> gasOf(context: CommandContext<S>, name: String): GasInput =
            context.getArgument(name, GasInput::class.java)
    }
}