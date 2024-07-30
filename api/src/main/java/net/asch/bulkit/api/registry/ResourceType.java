package net.asch.bulkit.api.registry;

import net.asch.bulkit.api.data.ResourceIdentifier;
import net.asch.bulkit.api.item.Disk;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
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
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class ResourceType<T> {
    public final String key;
    public final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceIdentifier<T>>> id;
    public final DeferredItem<Disk> disk;
    private final IDiskCapabilityRegister diskCapRegister;
    private final IDriveNetworkViewCapabilityRegister driveNetworkViewCapRegister;

    private ResourceType(String key, DeferredHolder<DataComponentType<?>, DataComponentType<ResourceIdentifier<T>>> id, DeferredItem<Disk> disk, IDiskCapabilityRegister diskCapRegister, IDriveNetworkViewCapabilityRegister driveNetworkViewCapRegister) {
        this.key = key;
        this.id = id;
        this.disk = disk;
        this.diskCapRegister = diskCapRegister;
        this.driveNetworkViewCapRegister = driveNetworkViewCapRegister;
    }

    public void registerDiskCapability(RegisterCapabilitiesEvent event) {
        diskCapRegister.register(event);
    }

    public <BE extends BlockEntity> void registerDriveNetworkViewCapability(RegisterCapabilitiesEvent event, BlockEntityType<BE> blockEntityType) {
        driveNetworkViewCapRegister.register(event, blockEntityType);
    }

    public static class Builder<T> implements Supplier<ResourceType<T>> {
        final String key;
        private final DeferredRegister.DataComponents dataComponents;
        private final DeferredRegister.Items items;
        private DeferredHolder<DataComponentType<?>, DataComponentType<ResourceIdentifier<T>>> id;
        private DeferredItem<Disk> disk;
        private IDiskCapabilityRegister diskCapRegister;
        private IDriveNetworkViewCapabilityRegister driveNetworkViewCapRegister;

        public Builder(String key, DeferredRegister.DataComponents dataComponents, DeferredRegister.Items items) {
            this.key = key;
            this.dataComponents = dataComponents;
            this.items = items;
        }

        public Builder<T> registry(Registry<T> registry) {
            id = dataComponents.registerComponentType("resource_" + key, (builder) -> builder.persistent(ResourceIdentifier.codec(registry)).networkSynchronized(ResourceIdentifier.streamCodec(registry)).cacheEncoding());
            return this;
        }

        public Builder<T> defaultDisk() {
            return disk(Disk::new);
        }

        public Builder<T> disk(Function<Item.Properties, Disk> sup) {
            disk = items.registerItem("disk_" + key, sup);
            return this;
        }

        public <H> Builder<T> diskHandler(ItemCapability<H, @Nullable Void> cap, ICapabilityProvider<ItemStack, @Nullable Void, H> provider) {
            diskCapRegister = (RegisterCapabilitiesEvent event) -> event.registerItem(cap, provider, disk);
            return this;
        }

        public <H, C> Builder<T> driveNetworkViewHandler(BlockCapability<H, C> cap, ICapabilityProvider<BlockEntity, C, H> provider) {
            driveNetworkViewCapRegister = (RegisterCapabilitiesEvent event, BlockEntityType<? extends BlockEntity> blockEntityType) -> event.registerBlockEntity(cap, blockEntityType, provider);
            return this;
        }

        @Override
        public ResourceType<T> get() {
            return new ResourceType<>(key, id, disk, diskCapRegister, driveNetworkViewCapRegister);
        }
    }

    @FunctionalInterface
    public interface IDiskCapabilityRegister {
        void register(RegisterCapabilitiesEvent event);
    }

    @FunctionalInterface
    public interface IDriveNetworkViewCapabilityRegister {
        void register(RegisterCapabilitiesEvent event, BlockEntityType<? extends BlockEntity> blockEntityType);
    }
}
