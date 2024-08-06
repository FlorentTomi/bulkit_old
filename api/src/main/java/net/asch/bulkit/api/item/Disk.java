package net.asch.bulkit.api.item;

import net.asch.bulkit.api.network.GuiPayloads;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class Disk extends Item {
    public Disk(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        PacketDistributor.sendToServer(GuiPayloads.Disk.INSTANCE);
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
    }
}
