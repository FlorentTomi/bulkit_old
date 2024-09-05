package net.asch.bulkit.api.block

import net.asch.bulkit.api.block.state.FilteredIntegerProperty
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

abstract class DriveNetworkViewBase(nSlots: Int, properties: Properties) : Block(properties), EntityBlock {
    init {
        registerDefaultState(stateDefinition.any().setValue(N_SLOTS_STATE, nSlots))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(N_SLOTS_STATE)
        super.createBlockStateDefinition(builder)
    }

    companion object {
        val AVAILABLE_SIZES: List<Int> = mutableListOf(1, 2, 4)
        val N_SLOTS_STATE: FilteredIntegerProperty = FilteredIntegerProperty.create("n_slots", AVAILABLE_SIZES)
    }
}