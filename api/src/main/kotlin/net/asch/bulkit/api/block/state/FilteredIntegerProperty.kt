package net.asch.bulkit.api.block.state

import com.google.common.collect.ImmutableSet
import net.minecraft.world.level.block.state.properties.Property
import java.util.*

class FilteredIntegerProperty private constructor(name: String, possibleValues: Collection<Int>) :
    Property<Int>(name, Int::class.java) {
    private val possibleValues: ImmutableSet<Int> = ImmutableSet.copyOf(possibleValues)

    private constructor(name: String, vararg possibleValues: Int) : this(name, possibleValues.toList())

    override fun getPossibleValues(): Collection<Int> = possibleValues

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this === other) {
            return true
        }

        if (other is FilteredIntegerProperty) {
            if (super.equals(other)) {
                return possibleValues == other.possibleValues
            }
        }

        return false
    }

    override fun getValue(value: String): Optional<Int> {
        try {
            val integer = value.toInt()
            return if (possibleValues.contains(integer)) Optional.of(integer) else Optional.empty()
        } catch (exc: NumberFormatException) {
            return Optional.empty()
        }
    }

    override fun getName(value: Int): String = value.toString()

    override fun generateHashCode(): Int {
        var result = super.generateHashCode()
        result = 31 * result + possibleValues.hashCode()
        return result
    }

    companion object {
        fun create(name: String, possibleValues: Collection<Int>): FilteredIntegerProperty =
            FilteredIntegerProperty(name, possibleValues)

        fun create(name: String, vararg possibleValues: Int): FilteredIntegerProperty =
            FilteredIntegerProperty(name, *possibleValues)
    }
}