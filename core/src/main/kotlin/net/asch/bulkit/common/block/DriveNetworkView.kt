package net.asch.bulkit.common.block

import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.asch.bulkit.common.block_entity.DriveNetworkViewEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction

class DriveNetworkView(nSlots: Int) : DriveNetworkViewBase(
    nSlots,
    Properties.of().destroyTime(0.5f).explosionResistance(1200.0f).sound(SoundType.METAL)
        .pushReaction(PushReaction.BLOCK)
) {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        DriveNetworkViewEntity(blockPos, blockState)
}