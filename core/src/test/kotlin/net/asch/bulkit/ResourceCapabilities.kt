package net.asch.bulkit

//import net.asch.bulkit.api.capability.Capabilities
//import net.asch.bulkit.api.capability.IDiskResourceHandler
//import net.asch.bulkit.api.registry.ResourceType
//import net.asch.bulkit.common.Resources
//import net.asch.bulkit.common.capability.disk.DiskFluidHandler
//import net.asch.bulkit.common.capability.disk.DiskItemHandler
//import net.minecraft.world.item.Item
//import net.minecraft.world.item.Items
//import net.minecraft.world.level.material.Fluid
//import net.minecraft.world.level.material.Fluids
//import net.neoforged.neoforge.capabilities.ItemCapability
//import net.neoforged.neoforge.fluids.FluidStack
//import net.neoforged.neoforge.fluids.FluidType
//import net.neoforged.neoforge.fluids.capability.IFluidHandler
//import net.neoforged.neoforge.items.IItemHandler
//import net.neoforged.testframework.junit.EphemeralTestServerProvider
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith

//@ExtendWith(EphemeralTestServerProvider::class)
object ResourceCapabilities {
//    @Test
//    fun item() {
//        val resourceType = Resources.ITEM.get()
//        val cap = net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.ITEM
//        assertInsert(resourceType, cap, false) { handler, resourceHandler ->
//            Assertions.assertEquals(handler.slots, 1)
//            Assertions.assertEquals(handler.getStackInSlot(0).isEmpty, true)
//
//            val capacity = DiskItemHandler.capacity(Items.REDSTONE.defaultMaxStackSize, resourceHandler)
//            val stackSize = capacity * 2
//            val toInsert = Items.REDSTONE.defaultInstance.copyWithCount(stackSize)
//            val remaining = handler.insertItem(0, toInsert, false)
//            Assertions.assertEquals(capacity, remaining.count)
//            Assertions.assertEquals(capacity, handler.getStackInSlot(0).count)
//        }
//
//        assertInsert(resourceType, cap, true) { handler, resourceHandler ->
//            Assertions.assertEquals(handler.slots, 1)
//            Assertions.assertEquals(handler.getStackInSlot(0).isEmpty, true)
//
//            val capacity = DiskItemHandler.capacity(Items.REDSTONE.defaultMaxStackSize, resourceHandler)
//            val stackSize = capacity * 2
//            val toInsert = Items.REDSTONE.defaultInstance.copyWithCount(stackSize)
//            val remaining = handler.insertItem(0, toInsert, false)
//            Assertions.assertTrue(remaining.isEmpty)
//            Assertions.assertEquals(capacity, handler.getStackInSlot(0).count)
//        }
//
//        val extractionTest = { resourceToExtract: Item, handler: IItemHandler ->
//            Assertions.assertEquals(handler.slots, 1)
//            Assertions.assertEquals(handler.getStackInSlot(0).isEmpty, true)
//
//            val stackSize = resourceToExtract.defaultMaxStackSize
//            val toInsert = resourceToExtract.defaultInstance.copyWithCount(stackSize)
//            handler.insertItem(0, toInsert, false)
//
//            val extracted = handler.extractItem(0, stackSize, false)
//            Assertions.assertEquals(stackSize, extracted.count)
//            Assertions.assertTrue(handler.getStackInSlot(0).isEmpty)
//        }
//
//        assertExtract(resourceType, cap, Items.REDSTONE, true, extractionTest)
//        assertExtract(resourceType, cap, Items.REDSTONE, false, extractionTest)
//    }
//
//    @Test
//    fun fluid() {
//        val resourceType = Resources.FLUID.get()
//        val cap = net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM
//        assertInsert(resourceType, cap, false) { handler, resourceHandler ->
//            Assertions.assertEquals(handler.tanks, 1)
//            Assertions.assertEquals(handler.getFluidInTank(0).isEmpty, true)
//
//            val capacity = DiskFluidHandler.capacity(resourceHandler)
//            val stackSize = capacity * 2
//            val toInsert = FluidStack(Fluids.WATER, stackSize)
//            val filled = handler.fill(toInsert, IFluidHandler.FluidAction.EXECUTE)
//            Assertions.assertEquals(capacity, filled)
//        }
//
//        assertInsert(resourceType, cap, true) { handler, resourceHandler ->
//            Assertions.assertEquals(handler.tanks, 1)
//            Assertions.assertEquals(handler.getFluidInTank(0).isEmpty, true)
//
//            val capacity = DiskFluidHandler.capacity(resourceHandler)
//            val stackSize = capacity * 2
//            val toInsert = FluidStack(Fluids.WATER, stackSize)
//            val filled = handler.fill(toInsert, IFluidHandler.FluidAction.EXECUTE)
//            Assertions.assertEquals(stackSize, filled)
//        }
//
//        val extractionTest = { resourceToExtract: Fluid, handler: IFluidHandler ->
//            Assertions.assertEquals(handler.tanks, 1)
//            Assertions.assertEquals(handler.getFluidInTank(0).isEmpty, true)
//
//            val stackSize = FluidType.BUCKET_VOLUME
//            val toInsert = FluidStack(resourceToExtract, stackSize)
//            handler.fill(toInsert, IFluidHandler.FluidAction.EXECUTE)
//
//            val extracted = handler.drain(stackSize, IFluidHandler.FluidAction.EXECUTE)
//            Assertions.assertEquals(stackSize, extracted.amount)
//            Assertions.assertTrue(handler.getFluidInTank(0).isEmpty)
//        }
//
//        assertExtract(resourceType, cap, Fluids.WATER, true, extractionTest)
//        assertExtract(resourceType, cap, Fluids.WATER, false, extractionTest)
//    }
//
//    private fun <H> assertInsert(
//        resourceType: ResourceType<*>,
//        cap: ItemCapability<H, Void?>,
//        isVoidExcess: Boolean,
//        tests: (H, IDiskResourceHandler) -> Unit
//    ) {
//        val disk = resourceType.disk.toStack()
//        Assertions.assertFalse(disk.has(resourceType.id.get()))
//
//        val diskResourceCap = disk.getCapability(Capabilities.Disk.RESOURCE)
//        diskResourceCap?.isVoidExcess = isVoidExcess
//
//        val diskCap = disk.getCapability(cap)
//        Assertions.assertNotNull(diskCap)
//        tests(diskCap!!, diskResourceCap!!)
//    }
//
//    private fun <T, H> assertExtract(
//        resourceType: ResourceType<T>,
//        cap: ItemCapability<H, Void?>,
//        resourceToExtract: T,
//        isLocked: Boolean,
//        tests: (T, H) -> Unit
//    ) {
//        val disk = resourceType.disk.toStack()
//        Assertions.assertFalse(disk.has(resourceType.id.get()))
//
//        val diskResourceCap = disk.getCapability(Capabilities.Disk.RESOURCE)
//        diskResourceCap?.isLocked = isLocked
//
//        val diskCap = disk.getCapability(cap)
//        Assertions.assertNotNull(diskCap)
//        tests(resourceToExtract, diskCap!!)
//
//        if (isLocked) {
//            Assertions.assertTrue(disk.has(resourceType.id.get()))
//            Assertions.assertEquals(resourceToExtract, disk.get(resourceType.id.get())!!.resource.value())
//        } else {
//            Assertions.assertFalse(disk.has(resourceType.id.get()))
//        }
//    }
}