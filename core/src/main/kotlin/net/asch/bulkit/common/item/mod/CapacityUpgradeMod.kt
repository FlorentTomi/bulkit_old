package net.asch.bulkit.common.item.mod

import net.asch.bulkit.common.item.BaseMod

class CapacityUpgradeMod(val multiplier: Int): BaseMod(Properties()) {
    companion object {
        val MULTIPLIERS: List<Int> = listOf(8, 16, 32)
    }
}