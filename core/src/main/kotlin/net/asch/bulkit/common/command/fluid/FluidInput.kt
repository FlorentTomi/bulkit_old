package net.asch.bulkit.common.command.fluid

import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack

class FluidInput(private val fluid: Holder<Fluid>, private val components: DataComponentMap) {
    fun createFluidStack(amount: Int): FluidStack {
        val fluidStack = FluidStack(fluid, amount)
        fluidStack.applyComponents(components)
        return fluidStack
    }

    fun serialize(levelRegistry: HolderLookup.Provider): String {
        val stringBuilder = StringBuilder(fluidName())
        val serializedComponents = serializeComponents(levelRegistry)
        if (serializedComponents.isNotEmpty()) {
            stringBuilder.append("[")
            stringBuilder.append(serializedComponents)
            stringBuilder.append("]")
        }

        return stringBuilder.toString()
    }

    private fun serializeComponents(levelRegistry: HolderLookup.Provider): String {
        val dynamicOps = levelRegistry.createSerializationContext(NbtOps.INSTANCE)
        return components.asSequence().map {
            val resourceLocation = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(it.type)
            Pair(resourceLocation, it.encodeValue(dynamicOps).result())
        }.filter { it.first != null && !it.second.isEmpty }.map { "${it.first}=${it.second.get()}" }.joinToString(",")
    }

    private fun fluidName(): String {
        return fluid.unwrapKey().map { it.location().toString() }.orElseGet { "unknown[$fluid]" }
    }
}
