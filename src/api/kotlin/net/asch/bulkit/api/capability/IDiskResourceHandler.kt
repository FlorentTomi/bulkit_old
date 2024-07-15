package net.asch.bulkit.api.capability

import net.neoforged.neoforge.items.IItemHandler

interface IDiskResourceHandler {
    var amount: Long
    var locked: Boolean
    var void: Boolean
    val mods: IItemHandler?

    fun multiplier(defaultMultiplier: Int): Int
}