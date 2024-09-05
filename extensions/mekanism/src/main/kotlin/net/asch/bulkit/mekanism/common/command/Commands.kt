package net.asch.bulkit.mekanism.common.command

import net.asch.bulkit.mekanism.BulkIt
import net.asch.bulkit.mekanism.common.command.gas.GasArgument
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object Commands {
    private val ARG_REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, BulkIt.ID)
    private val GAS_ARG = ARG_REGISTER.register("gas") { ->
        ArgumentTypeInfos.registerByClass(
            GasArgument::class.java, SingletonArgumentInfo.contextAware(GasArgument::gas)
        )
    }

    fun register(eventBus: IEventBus) = ARG_REGISTER.register(eventBus)
}