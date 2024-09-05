package net.asch.bulkit.common.command

import net.asch.bulkit.api.BulkItApi
import net.asch.bulkit.common.command.fluid.FluidArgument
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object Commands {
    private val ARG_REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, BulkItApi.ID)
    private val FLUID_ARG = ARG_REGISTER.register("fluid") { ->
        ArgumentTypeInfos.registerByClass(
            FluidArgument::class.java, SingletonArgumentInfo.contextAware(FluidArgument::fluid)
        )
    }

    fun register(eventBus: IEventBus) = ARG_REGISTER.register(eventBus)
}