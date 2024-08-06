package net.asch.bulkit.common.menu

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity

@Suppress("UNUSED_PARAMETER")
class DiskDriveMenu(containerId: Int, playerInventory: Inventory) :
    AbstractContainerMenu(Menus.DISK_DRIVE.get(), containerId) {

//    constructor(containerId: Int, playerInventory: Inventory, blockEntity: BlockEntity) : this(
//        containerId, playerInventory
//    ) {
//        val diskStorage = blockEntity.level?.getCapability(
//            Capabilities.DISK_STORAGE, blockEntity.blockPos, blockEntity.blockState, blockEntity, Direction.NORTH
//        ) ?: return

//        for (slotIdx in 0 until diskStorage.slots) {
//            val slot = SlotItemHandler(diskStorage, slotIdx, )
//        }
//    }

    override fun quickMoveStack(player: Player, slot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun stillValid(player: Player): Boolean {
        TODO("Not yet implemented")
    }
}