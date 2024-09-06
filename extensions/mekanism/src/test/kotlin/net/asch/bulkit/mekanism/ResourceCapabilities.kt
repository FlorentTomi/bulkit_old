package net.asch.bulkit.mekanism

import mekanism.api.Action
import mekanism.api.chemical.gas.Gas
import mekanism.api.chemical.gas.GasStack
import mekanism.api.chemical.gas.IGasHandler
import mekanism.common.registries.MekanismGases
import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.api.registry.ResourceType
import net.asch.bulkit.mekanism.common.capability.disk.DiskGasHandler
import net.neoforged.neoforge.capabilities.ItemCapability
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.testframework.junit.EphemeralTestServerProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(EphemeralTestServerProvider::class)
object ResourceCapabilities {
    @Test
    fun gasNonRadioactive() {
        gas(BulkIt.GAS_NON_RADIOACTIVE.get(), MekanismGases.OXYGEN.get(), MekanismGases.PLUTONIUM.get())
    }

    @Test
    fun gasRadioactive() {
        gas(BulkIt.GAS_RADIOACTIVE.get(), MekanismGases.PLUTONIUM.get(), MekanismGases.OXYGEN.get())
    }

    private fun gas(resourceType: ResourceType<Gas>, compatibleGasType: Gas, incompatibleGasType: Gas) {
        val diskCap = mekanism.common.capabilities.Capabilities.GAS.item

        assertInsert(resourceType, diskCap, false) { handler, resourceHandler ->
            Assertions.assertEquals(handler.tanks, 1)
            Assertions.assertEquals(handler.getChemicalInTank(0).isEmpty, true)

            val capacity = DiskGasHandler.capacity(resourceHandler)
            val stackSize = capacity * 2

            val toInsertIncompatible = GasStack(incompatibleGasType, stackSize)
            val remainingIncompatible = handler.insertChemical(toInsertIncompatible, Action.EXECUTE)
            Assertions.assertEquals(toInsertIncompatible, remainingIncompatible)
            Assertions.assertTrue(handler.getChemicalInTank(0).isEmpty)

            val toInsertCompatible = GasStack(compatibleGasType, stackSize)
            val remainingCompatible = handler.insertChemical(toInsertCompatible, Action.EXECUTE)
            Assertions.assertEquals(capacity, remainingCompatible.amount)
            Assertions.assertEquals(capacity, handler.getChemicalInTank(0).amount)
        }

        assertInsert(resourceType, diskCap, true) { handler, resourceHandler ->
            Assertions.assertEquals(handler.tanks, 1)
            Assertions.assertEquals(handler.getChemicalInTank(0).isEmpty, true)

            val capacity = DiskGasHandler.capacity(resourceHandler)
            val stackSize = capacity * 2
            val toInsert = GasStack(compatibleGasType, stackSize)
            val remaining = handler.insertChemical(toInsert, Action.EXECUTE)
            Assertions.assertTrue(remaining.isEmpty)
            Assertions.assertEquals(capacity, handler.getChemicalInTank(0).amount)
        }

        val extractionTest = { resourceToExtract: Gas, handler: IGasHandler ->
            Assertions.assertEquals(handler.tanks, 1)
            Assertions.assertEquals(handler.getChemicalInTank(0).isEmpty, true)

            val stackSize = FluidType.BUCKET_VOLUME.toLong()
            val toInsert = GasStack(resourceToExtract, stackSize)
            handler.insertChemical(toInsert, Action.EXECUTE)

            val extracted = handler.extractChemical(stackSize, Action.EXECUTE)
            Assertions.assertEquals(stackSize, extracted.amount)
            Assertions.assertTrue(handler.getChemicalInTank(0).isEmpty)
        }

        assertExtract(resourceType, diskCap, compatibleGasType, true, extractionTest)
        assertExtract(resourceType, diskCap, compatibleGasType, false, extractionTest)
    }

    private fun <H> assertInsert(
        resourceType: ResourceType<*>,
        cap: ItemCapability<H, Void?>,
        isVoidExcess: Boolean,
        tests: (H, IDiskResourceHandler) -> Unit
    ) {
        val disk = resourceType.disk.toStack()
        Assertions.assertFalse(disk.has(resourceType.id.get()))

        val diskResourceCap = disk.getCapability(Capabilities.Disk.RESOURCE)
        diskResourceCap?.isVoidExcess = isVoidExcess

        val diskCap = disk.getCapability(cap)
        Assertions.assertNotNull(diskCap)
        tests(diskCap!!, diskResourceCap!!)
    }

    private fun <T, H> assertExtract(
        resourceType: ResourceType<T>,
        cap: ItemCapability<H, Void?>,
        resourceToExtract: T,
        isLocked: Boolean,
        tests: (T, H) -> Unit
    ) {
        val disk = resourceType.disk.toStack()
        Assertions.assertFalse(disk.has(resourceType.id.get()))

        val diskResourceCap = disk.getCapability(Capabilities.Disk.RESOURCE)
        diskResourceCap?.isLocked = isLocked

        val diskCap = disk.getCapability(cap)
        Assertions.assertNotNull(diskCap)
        tests(resourceToExtract, diskCap!!)

        if (isLocked) {
            Assertions.assertTrue(disk.has(resourceType.id.get()))
            Assertions.assertEquals(resourceToExtract, disk.get(resourceType.id.get())!!.resource.value())
        } else {
            Assertions.assertFalse(disk.has(resourceType.id.get()))
        }
    }
}