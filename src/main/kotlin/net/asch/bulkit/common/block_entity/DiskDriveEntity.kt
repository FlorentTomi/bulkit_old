package net.asch.bulkit.common.block_entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class DiskDriveEntity(blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(BlockEntities.DISK_DRIVE.get(), blockPos, blockState)