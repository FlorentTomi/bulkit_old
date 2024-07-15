package net.asch.bulkit.common.data.delegate

import net.minecraft.core.component.DataComponentType
import net.neoforged.neoforge.common.MutableDataComponentHolder
import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ComponentDelegate<R, V : Any>(
    private val componentHolder: MutableDataComponentHolder, private val componentType: DataComponentType<V>
) : ReadWriteProperty<R, V?> {
    constructor(
        componentHolder: MutableDataComponentHolder,
        componentType: DeferredHolder<DataComponentType<*>, DataComponentType<V>>,
    ) : this(componentHolder, componentType.get())

    override fun getValue(thisRef: R, property: KProperty<*>): V? = componentHolder.get(componentType)

    override fun setValue(thisRef: R, property: KProperty<*>, value: V?) {
        componentHolder.set(componentType, value)
    }
}