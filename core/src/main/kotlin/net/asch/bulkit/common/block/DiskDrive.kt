package net.asch.bulkit.common.block

import net.asch.bulkit.client.text.LangEntries
import net.asch.bulkit.common.block_entity.DiskDriveEntity
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class DiskDrive(properties: Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DiskDriveEntity(blockPos, blockState)

    override fun useItemOn(
        pStack: ItemStack,
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHitResult: BlockHitResult
    ): ItemInteractionResult {
        if (pStack.`is`(Items.DRIVE_NETWORK_CONFIGURATOR)) {
            return useConfiguratorOn(pStack, pPos, pPlayer)
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult)
    }

    private fun useConfiguratorOn(configurator: ItemStack, pos: BlockPos, player: Player): ItemInteractionResult {
        if (configurator.has(DataComponents.CONFIGURATOR_ROOT_POS)) {
            configurator.remove(DataComponents.CONFIGURATOR_ROOT_POS)
            player.displayClientMessage(LangEntries.CONFIGURATOR_CLEAR_ROOT.component(), true)
        } else {
            configurator.set(DataComponents.CONFIGURATOR_ROOT_POS, pos)
            player.displayClientMessage(LangEntries.CONFIGURATOR_SET_ROOT.component(pos.toShortString()), true)
        }

        return ItemInteractionResult.CONSUME
    }
}