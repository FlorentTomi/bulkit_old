package net.asch.bulkit.common.command.fluid

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandBuildContext
import java.util.concurrent.CompletableFuture

class FluidArgument(context: CommandBuildContext) : ArgumentType<FluidInput> {
    private val parser = FluidParser(context)

    override fun parse(reader: StringReader): FluidInput {
        val fluidResult = parser.parse(reader)
        return FluidInput(fluidResult.fluid, fluidResult.components)
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>, builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> = parser.fillSuggestions(builder)

    override fun getExamples(): Collection<String> = EXAMPLES

    companion object {
        private val EXAMPLES = listOf("water", "minecraft:lava")

        fun fluid(context: CommandBuildContext): FluidArgument = FluidArgument(context)
        fun <S> fluidOf(context: CommandContext<S>, name: String): FluidInput =
            context.getArgument(name, FluidInput::class.java)
    }
}