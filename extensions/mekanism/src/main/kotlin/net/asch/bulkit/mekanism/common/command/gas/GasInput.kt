package net.asch.bulkit.mekanism.common.command.gas

import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup

class GasInput(private val gas: Holder<Gas>) {
    fun createGasStack(amount: Long): GasStack = GasStack(gas, amount)

    fun serialize(levelRegistry: HolderLookup.Provider): String {
        val stringBuilder = StringBuilder(gasName())
        return stringBuilder.toString()
    }

    private fun gasName(): String = gas.unwrapKey().map { it.location().toString() }.orElseGet { "unknown[$gas]" }
}