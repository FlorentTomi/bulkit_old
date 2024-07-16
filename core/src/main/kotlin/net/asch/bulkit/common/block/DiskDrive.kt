package net.asch.bulkit.common.block

import net.asch.bulkit.common.block_entity.DiskDriveEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction

class DiskDrive : Block(
    Properties.of().destroyTime(0.5f).explosionResistance(1200.0f).sound(SoundType.METAL)
        .pushReaction(PushReaction.BLOCK)
), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DiskDriveEntity(blockPos, blockState)
}