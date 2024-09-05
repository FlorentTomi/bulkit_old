package net.asch.bulkit.api.capability

import net.neoforged.neoforge.items.IItemHandler

interface IDiskResourceHandler {
    var amount: Long
    var isLocked: Boolean
    var isVoidExcess: Boolean
    val mods: IItemHandler?

    fun getMultiplier(defaultMultiplier: Int): Int
}