package net.asch.bulkit.common.block

import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.asch.bulkit.client.text.LangEntries
import net.asch.bulkit.common.block_entity.DriveNetworkViewEntity
import net.asch.bulkit.common.data.DataComponents
import net.asch.bulkit.common.item.Items
import net.asch.bulkit.network.DriveNetworkPayloads
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class DriveNetworkView(nSlots: Int, properties: Properties) : DriveNetworkViewBase(nSlots, properties) {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DriveNetworkViewEntity(blockPos, blockState)

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
            return useConfiguratorOn(pStack, pPos)
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult)
    }

    private fun useConfiguratorOn(configurator: ItemStack, pos: BlockPos): ItemInteractionResult {
        val rootPos = configurator.get(DataComponents.CONFIGURATOR_ROOT_POS)
            ?: return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        DriveNetworkPayloads.link(pos, rootPos)
        return ItemInteractionResult.CONSUME
    }
}