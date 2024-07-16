package net.asch.bulkit.common.block

import net.asch.bulkit.api.block.BulkItBlockStates
import net.asch.bulkit.common.block_entity.DriveNetworkViewEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction

class DriveNetworkView(nSlots: Int) : Block(
    Properties.of().destroyTime(0.5f).explosionResistance(1200.0f).sound(SoundType.METAL)
        .pushReaction(PushReaction.BLOCK)
), EntityBlock {
    init {
        registerDefaultState(
            stateDefinition.any().setValue(BulkItBlockStates.DriveNetworkView.N_SLOTS_STATE, nSlots)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BulkItBlockStates.DriveNetworkView.N_SLOTS_STATE)
        super.createBlockStateDefinition(builder)
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DriveNetworkViewEntity(blockPos, blockState)
}