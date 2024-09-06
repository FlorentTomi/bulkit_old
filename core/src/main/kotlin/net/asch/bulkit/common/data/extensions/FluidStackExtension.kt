package net.asch.bulkit.common.data.extensions

import net.asch.bulkit.api.data.ResourceIdentifier
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack

fun FluidStack.identifier(): ResourceIdentifier<Fluid> = ResourceIdentifier(fluidHolder, componentsPatch)
fun ResourceIdentifier<Fluid>.of(amount: Int): FluidStack = FluidStack(resource, amount, dataComponents)