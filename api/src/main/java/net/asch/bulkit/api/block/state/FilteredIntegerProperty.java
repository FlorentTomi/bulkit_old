package net.asch.bulkit.api.block.state;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class FilteredIntegerProperty extends Property<Integer> {
    private final ImmutableSet<Integer> possibleValues;

    protected FilteredIntegerProperty(String name, Collection<Integer> possibleValues) {
        super(name, Integer.class);
        this.possibleValues = ImmutableSet.copyOf(possibleValues);
    }

    protected FilteredIntegerProperty(String name, Integer... possibleValues) {
        super(name, Integer.class);
        this.possibleValues = ImmutableSet.copyOf(possibleValues);
    }

    public @NotNull Collection<Integer> getPossibleValues() {
        return possibleValues;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof FilteredIntegerProperty prop) {
            if (super.equals(other)) {
                return possibleValues.equals(prop.possibleValues);
            }
        }

        return false;
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + possibleValues.hashCode();
    }

    @Override
    public @NotNull Optional<Integer> getValue(@NotNull String value) {
        try {
            Integer integer = Integer.valueOf(value);
            return possibleValues.contains(integer) ? Optional.of(integer) : Optional.empty();
        } catch (NumberFormatException exc) {
            return Optional.empty();
        }
    }

    @Override
    public @NotNull String getName(@NotNull Integer value) {
        return value.toString();
    }

    public static FilteredIntegerProperty create(String name, Collection<Integer> possibleValues) {
        return new FilteredIntegerProperty(name, possibleValues);
    }

    public static FilteredIntegerProperty create(String name, Integer... possibleValues) {
        return new FilteredIntegerProperty(name, possibleValues);
    }
}