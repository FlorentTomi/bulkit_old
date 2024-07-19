package net.asch.bulkit.api.block;

import net.asch.bulkit.api.block.state.FilteredIntegerProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.Arrays;
import java.util.List;

public abstract class DriveNetworkViewBase extends Block implements EntityBlock {
    public static List<Integer> AVAILABLE_SIZES = Arrays.asList(1, 2, 4);
    public static FilteredIntegerProperty N_SLOTS_STATE = FilteredIntegerProperty.create("n_slots", AVAILABLE_SIZES);

    public DriveNetworkViewBase(int nSlots, BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(N_SLOTS_STATE, nSlots));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(N_SLOTS_STATE);
        super.createBlockStateDefinition(builder);
    }
}
