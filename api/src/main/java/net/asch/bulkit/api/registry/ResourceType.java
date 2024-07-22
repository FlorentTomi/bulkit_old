package net.asch.bulkit.api.registry;

import net.asch.bulkit.api.data.ResourceIdentifier;
import net.asch.bulkit.api.item.Disk;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public record ResourceType<T, DH, BH, BC>(String key,
                                          DeferredHolder<DataComponentType<?>, DataComponentType<ResourceIdentifier<T>>> resource,
                                          DeferredItem<Disk> disk, ItemCapability<DH, Void> diskCap,
                                          ICapabilityProvider<ItemStack, Void, DH> diskCapProvider,
                                          BlockCapability<BH, BC> driveNetworkViewCap,
                                          ICapabilityProvider<BlockEntity, BC, BH> driveNetworkViewCapProvider) {
    public void registerDiskCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(diskCap, diskCapProvider, disk);
    }

    public <BE extends BlockEntity> void registerDriveNetworkViewCapability(RegisterCapabilitiesEvent event, BlockEntityType<BE> blockEntityType) {
        event.registerBlockEntity(driveNetworkViewCap, blockEntityType, driveNetworkViewCapProvider);
    }

    public void registerToCreativeTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        output.accept(disk);
    }

    public static class Builder<T, DH, BH, BC> implements Supplier<ResourceType<T, DH, BH, BC>> {
        final String key;
        private final DeferredRegister.DataComponents dataComponents;
        private final DeferredRegister.Items items;
        private DeferredHolder<DataComponentType<?>, DataComponentType<ResourceIdentifier<T>>> resource;
        private DeferredItem<Disk> disk;
        private ItemCapability<DH, Void> diskCap;
        private ICapabilityProvider<ItemStack, Void, DH> diskCapProvider;
        private BlockCapability<BH, BC> driveNetworkViewCap;
        private ICapabilityProvider<BlockEntity, BC, BH> driveNetworkViewCapProvider;

        public Builder(String key, DeferredRegister.DataComponents dataComponents, DeferredRegister.Items items) {
            this.key = key;
            this.dataComponents = dataComponents;
            this.items = items;
        }

        public Builder<T, DH, BH, BC> registry(Registry<T> registry) {
            resource = dataComponents.registerComponentType("resource_" + key, (builder) -> builder.persistent(ResourceIdentifier.codec(registry)).networkSynchronized(ResourceIdentifier.streamCodec(registry)).cacheEncoding());
            return this;
        }

        public Builder<T, DH, BH, BC> defaultDisk() {
            return disk((props) -> new Disk(props.stacksTo(16)));
        }

        public Builder<T, DH, BH, BC> disk(Function<Item.Properties, Disk> sup) {
            disk = items.registerItem("disk_" + key, sup);
            return this;
        }

        public Builder<T, DH, BH, BC> diskHandler(ItemCapability<DH, Void> cap, ICapabilityProvider<ItemStack, Void, DH> provider) {
            diskCap = cap;
            diskCapProvider = provider;
            return this;
        }

        public Builder<T, DH, BH, BC> driveNetworkViewHandler(BlockCapability<BH, BC> cap, ICapabilityProvider<BlockEntity, BC, BH> provider) {
            driveNetworkViewCap = cap;
            driveNetworkViewCapProvider = provider;
            return this;
        }

        @Override
        public ResourceType<T, DH, BH, BC> get() {
            return new ResourceType<>(key, resource, disk, diskCap, diskCapProvider, driveNetworkViewCap, driveNetworkViewCapProvider);
        }
    }
}
