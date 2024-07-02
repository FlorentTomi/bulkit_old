package net.asch.bulkit.common.data.resource

import net.asch.bulkit.common.data.ResourceIdentifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

fun ItemStack.identifier(): ResourceIdentifier<Item> = ResourceIdentifier(itemHolder, componentsPatch)
fun ResourceIdentifier<Item>.of(amount: Long): ItemStack = ItemStack(resource, amount.toInt(), dataComponents)