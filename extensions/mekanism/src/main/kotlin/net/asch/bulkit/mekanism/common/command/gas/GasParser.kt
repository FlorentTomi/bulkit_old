package net.asch.bulkit.mekanism.common.command.gas

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import mekanism.api.chemical.gas.Gas
import mekanism.common.registries.MekanismGases
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.*
import java.util.concurrent.CompletableFuture

class GasParser(registries: HolderLookup.Provider) {
    private val gases = registries.lookupOrThrow(MekanismGases.GASES.registryKey)
    private val registryOps = registries.createSerializationContext(NbtOps.INSTANCE)

    fun parse(reader: StringReader): GasResult {
        var gasOpt: Holder<Gas>? = null
        parse(reader, object : Visitor {
            override fun visitGas(gas: Holder<Gas>) {
                gasOpt = gas
            }

            override fun visitSuggestions(suggestions: (builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>) {

            }
        })

        if (gasOpt == null) {
            throw NullPointerException("Parser gave no gas")
        }

        return GasResult(gasOpt!!)
    }

    fun fillSuggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input)
        reader.cursor = builder.start

        val suggestionVisitor = SuggestionVisitor()
        val state = State(gases, reader, suggestionVisitor)
        try {
            state.parse()
        } catch (_: CommandSyntaxException) {

        }

        return suggestionVisitor.resolveSuggestions(builder, reader)
    }

    private fun parse(reader: StringReader, visitor: Visitor) {
        val i = reader.cursor

        try {
            State(gases, reader, visitor).parse()
        } catch (exc: CommandSyntaxException) {
            reader.cursor = i
            throw exc
        }
    }

    data class GasResult(val gas: Holder<Gas>)

    private interface Visitor {
        fun visitGas(gas: Holder<Gas>)
        fun visitSuggestions(suggestions: (builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>)
    }

    private class SuggestionVisitor : Visitor {
        private var suggestions = SUGGEST_NOTHING

        override fun visitGas(gas: Holder<Gas>) {

        }

        override fun visitSuggestions(suggestions: (builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
            this.suggestions = suggestions
        }

        fun resolveSuggestions(builder: SuggestionsBuilder, reader: StringReader): CompletableFuture<Suggestions> =
            suggestions(builder.createOffset(reader.cursor))
    }

    private class State(
        private val gases: HolderLookup.RegistryLookup<Gas>,
        private val reader: StringReader,
        private val visitor: Visitor
    ) {
        fun parse() {
            visitor.visitSuggestions(this::suggestGas)
            readGas()
        }

        private fun readGas() {
            val i = reader.cursor
            val resourceLocation = ResourceLocation.read(reader)
            visitor.visitGas(
                gases.get(ResourceKey.create(MekanismGases.GASES.registryKey, resourceLocation)).orElseThrow {
                    reader.cursor = i
                    ERROR_UNKNOWN_GAS.createWithContext(reader, resourceLocation)
                })
        }

        private fun suggestGas(builder: SuggestionsBuilder): CompletableFuture<Suggestions> =
            SharedSuggestionProvider.suggestResource(gases.listElementIds().map(ResourceKey<*>::location), builder)
    }

    companion object {
        private val ERROR_UNKNOWN_GAS = DynamicCommandExceptionType {
            Component.translatableEscape("argument.item.id.invalid", *arrayOf(it))
        }

        private val SUGGEST_NOTHING = { obj: SuggestionsBuilder -> obj.buildFuture() }
    }
}