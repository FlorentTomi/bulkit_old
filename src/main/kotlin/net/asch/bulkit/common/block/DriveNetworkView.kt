package net.asch.bulkit.common.block

import net.asch.bulkit.common.block_entity.DriveNetworkViewEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.PushReaction

class DriveNetworkView(nSlots: Int) : Block(
    Properties.of().destroyTime(0.5f).explosionResistance(1200.0f).sound(SoundType.METAL)
        .pushReaction(PushReaction.BLOCK)
), EntityBlock {
    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(N_SLOTS_STATE, nSlots)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(N_SLOTS_STATE)
        super.createBlockStateDefinition(builder)
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DriveNetworkViewEntity(blockPos, blockState)

    companion object {
        val AVAILABLE_SIZES: List<Int> = listOf(1, 2, 4)
        val N_SLOTS_STATE: IntegerProperty =
            IntegerProperty.create("n_slots", AVAILABLE_SIZES.min(), AVAILABLE_SIZES.max())
    }
}