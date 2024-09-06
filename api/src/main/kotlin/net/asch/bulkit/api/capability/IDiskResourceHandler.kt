package net.asch.bulkit.api.capability

import net.neoforged.neoforge.items.IItemHandler

interface IDiskResourceHandler {
    var amountI: Int
    var amountL: Long

    var isLocked: Boolean
    var isVoidExcess: Boolean
    val mods: IItemHandler?

    fun getMultiplier(defaultMultiplier: Int): Int
}