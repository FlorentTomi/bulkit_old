package net.asch.bulkit.common.data.resource

import net.asch.bulkit.common.data.ResourceIdentifier
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack

fun FluidStack.identifier(): ResourceIdentifier<Fluid> = ResourceIdentifier(fluidHolder, componentsPatch)
fun ResourceIdentifier<Fluid>.of(amount: Long): FluidStack = FluidStack(resource, amount.toInt(), dataComponents)