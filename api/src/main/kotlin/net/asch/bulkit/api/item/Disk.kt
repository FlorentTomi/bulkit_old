package net.asch.bulkit.api.item

import net.asch.bulkit.api.network.GuiPayloads
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor

class Disk(properties: Properties) : Item(properties.stacksTo(16)) {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        PacketDistributor.sendToServer(GuiPayloads.Disk.INSTANCE)
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }
}