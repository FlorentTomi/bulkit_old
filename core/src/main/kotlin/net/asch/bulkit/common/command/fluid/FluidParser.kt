package net.asch.bulkit.common.command.fluid

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagParser
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import java.util.*
import java.util.concurrent.CompletableFuture

class FluidParser(registries: HolderLookup.Provider) {
    private val fluids = registries.lookupOrThrow(Registries.FLUID)
    private val registryOps = registries.createSerializationContext(NbtOps.INSTANCE)

    fun parse(reader: StringReader): FluidResult {
        var fluidOpt: Holder<Fluid>? = null
        val componentMapBuilder = DataComponentMap.builder()
        parse(reader, object : Visitor {
            override fun visitFluid(fluid: Holder<Fluid>) {
                fluidOpt = fluid
            }

            override fun <T> visitComponent(componentType: DataComponentType<T>, value: T) {
                componentMapBuilder.set(componentType, value)
            }

            override fun visitSuggestions(suggestions: (builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>) {

            }
        })
        if (fluidOpt == null) {
            throw NullPointerException("Parser gave no fluid")
        }

        val componentMap = componentMapBuilder.build()
        return FluidResult(fluidOpt!!, componentMap)
    }

    fun fillSuggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input)
        reader.cursor = builder.start

        val suggestionVisitor = SuggestionVisitor()
        val state = State(fluids, registryOps, reader, suggestionVisitor)
        try {
            state.parse()
        } catch (_: CommandSyntaxException) {

        }

        return suggestionVisitor.resolveSuggestions(builder, reader)
    }

    private fun parse(reader: StringReader, visitor: Visitor) {
        val i = reader.cursor

        try {
            State(fluids, registryOps, reader, visitor).parse()
        } catch (exc: CommandSyntaxException) {
            reader.cursor = i
            throw exc
        }
    }

    data class FluidResult(val fluid: Holder<Fluid>, val components: DataComponentMap)

    private interface Visitor {
        fun visitFluid(fluid: Holder<Fluid>)
        fun <T> visitComponent(componentType: DataComponentType<T>, value: T)
        fun visitSuggestions(suggestions: (builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>)
    }

    private class SuggestionVisitor : Visitor {
        private var suggestions = SUGGEST_NOTHING

        override fun visitFluid(fluid: Holder<Fluid>) {

        }

        override fun <T> visitComponent(componentType: DataComponentType<T>, value: T) {

        }

        override fun visitSuggestions(suggestions: (builder: SuggestionsBuilder) -> CompletableFuture<Suggestions>) {
            this.suggestions = suggestions
        }

        fun resolveSuggestions(builder: SuggestionsBuilder, reader: StringReader): CompletableFuture<Suggestions> =
            suggestions(builder.createOffset(reader.cursor))
    }

    private class State(
        private val fluids: HolderLookup.RegistryLookup<Fluid>,
        private val registryOps: RegistryOps<Tag>,
        private val reader: StringReader,
        private val visitor: Visitor
    ) {
        fun parse() {
            visitor.visitSuggestions(this::suggestFluid)
            readFluid()
            visitor.visitSuggestions(this::suggestStartComponents)
            if (reader.canRead() && reader.peek() == '[') {
                visitor.visitSuggestions(SUGGEST_NOTHING)
                readComponents()
            }
        }

        private fun readFluid() {
            val i = reader.cursor
            val resourceLocation = ResourceLocation.read(reader)
            visitor.visitFluid(fluids.get(ResourceKey.create(Registries.FLUID, resourceLocation)).orElseThrow {
                reader.cursor = i
                ERROR_UNKNOWN_FLUID.createWithContext(reader, resourceLocation)
            })
        }

        private fun readComponents() {
            reader.expect('[')
            visitor.visitSuggestions(this::suggestComponentAssignment)

            val componentTypes = mutableSetOf<DataComponentType<*>?>()
            while (reader.canRead() && reader.peek() != ']') {
                reader.skipWhitespace()
                val componentType = readComponentType(reader)
                if (!componentTypes.add(componentType)) {
                    throw ERROR_REPEATED_COMPONENT.create(componentType)
                }

                visitor.visitSuggestions(this::suggestAssignment)
                reader.skipWhitespace()
                reader.expect('=')
                visitor.visitSuggestions(SUGGEST_NOTHING)
                reader.skipWhitespace()
                readComponent(componentType)
                reader.skipWhitespace()
                visitor.visitSuggestions(this::suggestNextOrEndComponents)
                if (!reader.canRead() || reader.peek() != ',') {
                    break
                }

                reader.skip()
                reader.skipWhitespace()
                visitor.visitSuggestions(this::suggestComponentAssignment)
                if (!reader.canRead()) {
                    throw ERROR_EXPECTED_COMPONENT.createWithContext(reader)
                }

                reader.expect(']')
                visitor.visitSuggestions(SUGGEST_NOTHING)
            }
        }

        private fun <T> readComponent(componentType: DataComponentType<T>) {
            val i = reader.cursor
            val tag = TagParser(reader).readValue()
            val dataResult = componentType.codecOrThrow().parse(registryOps, tag)
            visitor.visitComponent(componentType, dataResult.getOrThrow {
                reader.cursor = i
                ERROR_MALFORMED_COMPONENT.createWithContext(reader, componentType.toString(), it)
            })
        }

        private fun suggestStartComponents(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            if (builder.remaining.isEmpty()) {
                builder.suggest("[")
            }

            return builder.buildFuture()
        }

        private fun suggestNextOrEndComponents(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            if (builder.remaining.isEmpty()) {
                builder.suggest(",")
                builder.suggest("]")
            }

            return builder.buildFuture()
        }

        private fun suggestAssignment(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            if (builder.remaining.isEmpty()) {
                builder.suggest("=")
            }

            return builder.buildFuture()
        }

        private fun suggestFluid(builder: SuggestionsBuilder): CompletableFuture<Suggestions> =
            SharedSuggestionProvider.suggestResource(fluids.listElementIds().map(ResourceKey<*>::location), builder)

        private fun suggestComponentAssignment(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            val s = builder.remaining.lowercase(Locale.ROOT)
            SharedSuggestionProvider.filterResources(BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet(),
                s,
                { it.key.location() }) {
                val componentType = it.value
                if (componentType.codec() != null) {
                    val resourceLocation = it.key.location()
                    builder.suggest("$resourceLocation=")
                }
            }

            return builder.buildFuture()
        }

        companion object {
            private fun readComponentType(reader: StringReader): DataComponentType<*> {
                if (!reader.canRead()) {
                    throw ERROR_EXPECTED_COMPONENT.createWithContext(reader)
                } else {
                    val i = reader.cursor
                    val resourceLocation = ResourceLocation.read(reader)
                    val componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourceLocation)
                    if (componentType != null && !componentType.isTransient) {
                        return componentType
                    }

                    reader.cursor = i
                    throw ERROR_UNKNOWN_COMPONENT.createWithContext(reader, resourceLocation)
                }
            }
        }
    }

    companion object {
        private val ERROR_UNKNOWN_FLUID = DynamicCommandExceptionType {
            Component.translatableEscape("argument.item.id.invalid", *arrayOf(it))
        }

        private val ERROR_REPEATED_COMPONENT = DynamicCommandExceptionType {
            Component.translatableEscape("arguments.item.component.repeated", *arrayOf(it))
        }

        private val ERROR_EXPECTED_COMPONENT =
            SimpleCommandExceptionType(Component.translatable("arguments.item.component.expected"))

        private val ERROR_UNKNOWN_COMPONENT = DynamicCommandExceptionType {
            Component.translatableEscape("arguments.item.component.unknown", *arrayOf(it))
        }

        private val ERROR_MALFORMED_COMPONENT = Dynamic2CommandExceptionType { it0: Any, it1: Any ->
            Component.translatableEscape("arguments.item.component.malformed", *arrayOf(it0, it1))
        }

        private val SUGGEST_NOTHING = { obj: SuggestionsBuilder -> obj.buildFuture() }
    }
}
