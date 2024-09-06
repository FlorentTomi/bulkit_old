package net.asch.bulkit.network

import net.asch.bulkit.BulkIt
import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.capability.Capabilities
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object DiskPayloads {
    fun register(registrar: PayloadRegistrar) {
        registrar.playToServer(AddItem.TYPE, AddItem.STREAM_CODEC, ::handlePayload)
        registrar.playToServer(AddFluid.TYPE, AddFluid.STREAM_CODEC, ::handlePayload)
        registrar.playToServer(AddEnergy.TYPE, AddEnergy.STREAM_CODEC, ::handlePayload)
        registrar.playToServer(Grow.TYPE, Grow.STREAM_CODEC, ::handlePayload)
        registrar.playToServer(Shrink.TYPE, Shrink.STREAM_CODEC, ::handlePayload)
        registrar.playToServer(Lock.TYPE, Lock.STREAM_CODEC, ::handlePayload)
        registrar.playToServer(Void.TYPE, Void.STREAM_CODEC, ::handlePayload)
    }

    fun addItem(stack: ItemStack) = BulkItApi.sendToServer(AddItem(stack))
    fun addFluid(stack: FluidStack) = BulkItApi.sendToServer(AddFluid(stack))
    fun addEnergy(amount: Int) = BulkItApi.sendToServer(AddEnergy(amount))
    fun grow(amount: Long) = BulkItApi.sendToServer(Grow(amount))
    fun shrink(amount: Long) = BulkItApi.sendToServer(Shrink(amount))
    fun lock(locked: Boolean) = BulkItApi.sendToServer(Lock(locked))
    fun void(void: Boolean) = BulkItApi.sendToServer(Void(void))

    private inline fun <reified PayloadType : CustomPacketPayload> handlePayload(
        payload: PayloadType, context: IPayloadContext
    ) {
        val player = context.player()
        val mainHandItem = player.mainHandItem
        when (payload.type()) {
            AddItem.TYPE -> {
                val diskCapability =
                    mainHandItem.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM)
                diskCapability?.insertItem(0, (payload as AddItem).stack, false)
            }

            AddFluid.TYPE -> {
                val diskCapability =
                    mainHandItem.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM)
                diskCapability?.fill((payload as AddFluid).stack, IFluidHandler.FluidAction.EXECUTE)
            }

            AddEnergy.TYPE -> {
                val diskCapability =
                    mainHandItem.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM)
                diskCapability?.receiveEnergy((payload as AddEnergy).amount, false)
            }

            Grow.TYPE -> {
                val diskCapability = mainHandItem.getCapability(Capabilities.Disk.RESOURCE)
                diskCapability?.let { diskCapability.amountL += (payload as Grow).amount }
            }

            Shrink.TYPE -> {
                val diskCapability = mainHandItem.getCapability(Capabilities.Disk.RESOURCE)
                diskCapability?.let { diskCapability.amountL -= (payload as Shrink).amount }
            }

            Lock.TYPE -> {
                val diskCapability = mainHandItem.getCapability(Capabilities.Disk.RESOURCE)
                diskCapability?.isLocked = (payload as Lock).locked
            }

            Void.TYPE -> {
                val diskCapability = mainHandItem.getCapability(Capabilities.Disk.RESOURCE)
                diskCapability?.isVoidExcess = (payload as Void).void
            }
        }
    }

    private class AddItem(val stack: ItemStack) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<AddItem>(BulkIt.location("disk_add_item"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AddItem> = StreamCodec.composite(
                ItemStack.OPTIONAL_STREAM_CODEC, AddItem::stack, ::AddItem
            )
        }
    }

    private class AddFluid(val stack: FluidStack) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<AddFluid>(BulkIt.location("disk_add_fluid"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AddFluid> = StreamCodec.composite(
                FluidStack.OPTIONAL_STREAM_CODEC, AddFluid::stack, ::AddFluid
            )
        }
    }

    private class AddEnergy(val amount: Int) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<AddEnergy>(BulkIt.location("disk_add_energy"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AddEnergy> = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, AddEnergy::amount, ::AddEnergy
            )
        }
    }

    private class Grow(val amount: Long) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<Grow>(BulkIt.location("disk_grow"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Grow> = StreamCodec.composite(
                ByteBufCodecs.VAR_LONG, Grow::amount, ::Grow
            )
        }
    }

    private class Shrink(val amount: Long) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<Shrink>(BulkIt.location("disk_shrink"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Shrink> = StreamCodec.composite(
                ByteBufCodecs.VAR_LONG, Shrink::amount, ::Shrink
            )
        }
    }

    private class Lock(val locked: Boolean) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<Lock>(BulkIt.location("disk_lock"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Lock> = StreamCodec.composite(
                ByteBufCodecs.BOOL, Lock::locked, ::Lock
            )
        }
    }

    private class Void(val void: Boolean) : CustomPacketPayload {
        override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

        companion object {
            val TYPE = CustomPacketPayload.Type<Void>(BulkIt.location("disk_void"))
            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Void> = StreamCodec.composite(
                ByteBufCodecs.BOOL, Void::void, ::Void
            )
        }
    }
}