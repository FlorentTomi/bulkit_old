package net.asch.bulkit.api.block

import net.minecraft.world.level.block.state.properties.IntegerProperty

object BulkItBlockStates {
    object DriveNetworkView {
        val AVAILABLE_SIZES: List<Int> = listOf(1, 2, 4)
        val N_SLOTS_STATE: IntegerProperty =
            IntegerProperty.create("n_slots", AVAILABLE_SIZES.min(), AVAILABLE_SIZES.max())
    }
}