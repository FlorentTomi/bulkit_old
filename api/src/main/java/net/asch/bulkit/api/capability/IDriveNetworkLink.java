package net.asch.bulkit.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IDriveNetworkLink {
    int UNMAPPED_SLOT = -1;

    default void unmap(int slot) {
        map(slot, UNMAPPED_SLOT);
    }

    void map(int slot, int rootSlot);

    default void unlink() {
        linkTo(null);
    }

    void linkTo(@Nullable BlockPos blockPos);

    ItemStack disk(int slot);
}
