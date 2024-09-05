package net.asch.bulkit.common.capability.disk

import net.asch.bulkit.api.capability.Capabilities
import net.asch.bulkit.api.capability.IDiskResourceHandler
import net.asch.bulkit.client.text.LangEntries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.energy.IEnergyStorage

class DiskEnergyHandler(disk: ItemStack, private val baseCapacity: Int) : IEnergyStorage {
    private val resource = disk.getCapability(Capabilities.Disk.RESOURCE)!!

    override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
        val capacity = capacity(baseCapacity, resource)
        if (canReceive() && toReceive > 0) {
            val energyReceived = (capacity - resource.amount).coerceIn(0, minOf(capacity.toLong(), toReceive.toLong()))

            if (!simulate) {
                resource.amount += energyReceived
            }

            return energyReceived.toInt()
        } else {
            return 0
        }
    }

    override fun extractEnergy(toExtract: Int, simulate: Boolean): Int {
        val capacity = capacity(baseCapacity, resource)
        if (canExtract() && toExtract > 0) {
            val energyExtracted = minOf(resource.amount, minOf(capacity, toExtract).toLong())
            if (!simulate) {
                resource.amount -= energyExtracted
            }

            return energyExtracted.toInt()
        } else {
            return 0
        }
    }

    override fun getEnergyStored(): Int = resource.amount.toInt()
    override fun getMaxEnergyStored(): Int = capacity(baseCapacity, resource)
    override fun canExtract(): Boolean = true
    override fun canReceive(): Boolean = true

    companion object {
        const val BASE_CAPACITY: Int = 16000
        const val DEFAULT_MULTIPLIER: Int = 1

        fun capacity(baseCapacity: Int, resource: IDiskResourceHandler): Int = baseCapacity * resource.getMultiplier(DEFAULT_MULTIPLIER)
        fun build(disk: ItemStack, ctx: Void?) = DiskEnergyHandler(disk, BASE_CAPACITY)
    }
}