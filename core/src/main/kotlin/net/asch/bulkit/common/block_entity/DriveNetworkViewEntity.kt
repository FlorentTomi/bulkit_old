package net.asch.bulkit.common.block_entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class DriveNetworkViewEntity(blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(BlockEntities.DRIVE_NETWORK_VIEW.get(), blockPos, blockState)