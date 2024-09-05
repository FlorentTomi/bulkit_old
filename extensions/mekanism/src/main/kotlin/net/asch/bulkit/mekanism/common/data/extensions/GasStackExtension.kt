package net.asch.bulkit.mekanism.common.data.extensions

import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import net.asch.bulkit.api.data.ResourceIdentifier
import net.minecraft.core.component.DataComponentPatch

fun GasStack.identifier(): ResourceIdentifier<Gas> = ResourceIdentifier(chemicalHolder, DataComponentPatch.EMPTY)
fun ResourceIdentifier<Gas>.of(amount: Long): GasStack = GasStack(resource, amount)