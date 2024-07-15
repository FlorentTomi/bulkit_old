package net.asch.bulkit.common.data.delegate

import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import kotlin.jvm.optionals.getOrNull
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AttachmentDelegate<R, V : Any>(
    private val attachmentHolder: AttachmentHolder, private val attachmentType: AttachmentType<V>
) : ReadWriteProperty<R, V?> {
    constructor(
        attachmentHolder: AttachmentHolder, attachmentType: DeferredHolder<AttachmentType<*>, AttachmentType<V>>
    ) : this(attachmentHolder, attachmentType.get())

    override fun getValue(thisRef: R, property: KProperty<*>): V? =
        attachmentHolder.getExistingData(attachmentType).getOrNull()

    override fun setValue(thisRef: R, property: KProperty<*>, value: V?) {
        if (value == null) {
            attachmentHolder.removeData(attachmentType)
        } else {
            attachmentHolder.setData(attachmentType, value)
        }
    }
}