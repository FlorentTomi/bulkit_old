package net.asch.bulkit.common.data.delegate

import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DefaultedAttachmentDelegate<R, V : Any>(
    private val attachmentHolder: AttachmentHolder,
    private val attachmentType: AttachmentType<V>,
    private val defaultValue: V
) : ReadWriteProperty<R, V> {
    constructor(
        attachmentHolder: AttachmentHolder,
        attachmentType: DeferredHolder<AttachmentType<*>, AttachmentType<V>>,
        defaultValue: V
    ) : this(attachmentHolder, attachmentType.get(), defaultValue)

    override fun getValue(thisRef: R, property: KProperty<*>): V =
        attachmentHolder.getExistingData(attachmentType).orElse(defaultValue)

    override fun setValue(thisRef: R, property: KProperty<*>, value: V) {
        attachmentHolder.setData(attachmentType, value)
    }
}