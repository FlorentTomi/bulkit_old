package net.asch.bulkit.common.item

import net.minecraft.world.item.Item

abstract class BaseMod(properties: Properties): Item(properties.stacksTo(16))