package net.asch.bulkit.common.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity

class DriveNetworkConfigurator(properties: Properties) : Item(properties.rarity(Rarity.UNCOMMON).stacksTo(1))