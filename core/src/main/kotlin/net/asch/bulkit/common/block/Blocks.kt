package net.asch.bulkit.common.block

import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.api.block.DriveNetworkViewBase
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.material.PushReaction
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object Blocks {
    private val REGISTER = DeferredRegister.createBlocks(BulkItApi.ID)
    private val BASE_BLOCK_PROPERTIES =
        Properties.of().destroyTime(0.5f).explosionResistance(1200.0f).sound(SoundType.METAL)
            .pushReaction(PushReaction.BLOCK)

    private val DISK_DRIVE: DeferredBlock<DiskDrive> =
        REGISTER.registerBlock("disk_drive", { props -> DiskDrive(props) }, BASE_BLOCK_PROPERTIES)

    private val DRIVE_NETWORK_VIEWS = DriveNetworkViewBase.AVAILABLE_SIZES.associateWith { size ->
        REGISTER.registerBlock(
            "drive_network_view_$size", { props -> DriveNetworkView(size, props) }, BASE_BLOCK_PROPERTIES
        )
    }

    fun register(eventBus: IEventBus) = REGISTER.register(eventBus)

    fun registerBlockItems(register: DeferredRegister.Items) {
        register.registerSimpleBlockItem("disk_drive", DISK_DRIVE)
        DRIVE_NETWORK_VIEWS.forEach { (size, view) ->
            register.registerSimpleBlockItem("drive_network_view_$size", view)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun registerToCreativeTab(params: ItemDisplayParameters, output: Output) {
        output.accept(DISK_DRIVE)
        DRIVE_NETWORK_VIEWS.forEach { (_, view) -> output.accept(view) }
    }
}