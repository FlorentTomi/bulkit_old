package net.asch.bulkit.api.item;

import net.minecraft.world.item.Item;

public class Disk extends Item {
    public Disk(Properties properties) {
        super(properties.stacksTo(16));
    }
}
