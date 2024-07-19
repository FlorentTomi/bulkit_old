package net.asch.bulkit.api.capability;

import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IDiskResourceHandler {
    long getAmount();
    void setAmount(long value);
    boolean isLocked();
    void setLocked(boolean value);
    boolean isVoidExcess();
    void setVoidExcess(boolean value);
    @Nullable IItemHandler mods();

    int multiplier(int defaultMultiplier);
}
