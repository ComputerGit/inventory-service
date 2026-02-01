package com.at.t.ecommerce.inventory.domain.stock.vo;

import java.util.Objects;
import com.at.t.ecommerce.inventory.domain.stock.enums.UnitOfMeasure;

/**
 * A Fail-Safe Value Object for handling amounts.
 * Enforces that you cannot add/subtract mismatched units.
 */
public record Quantity(long value, UnitOfMeasure unit) implements Comparable<Quantity> {

    // 1. COMPACT CONSTRUCTOR (Validation)
    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Objects.requireNonNull(unit, "Unit of Measure cannot be null");
    }

    // 2. FAIL-SAFE MATH
    public Quantity add(Quantity other) {
        checkUnitCompatibility(other);
        return new Quantity(this.value + other.value, this.unit);
    }

    public Quantity subtract(Quantity other) {
        checkUnitCompatibility(other);
        long result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("Subtraction results in negative quantity");
        }
        return new Quantity(result, this.unit);
    }

    // 3. COMPARISON LOGIC
    public boolean isGreaterThan(Quantity other) {
        checkUnitCompatibility(other);
        return this.value > other.value;
    }
    
    public boolean isLessThan(Quantity other) {
        checkUnitCompatibility(other);
        return this.value < other.value;
    }
    
    public boolean isZero() {
        return this.value == 0;
    }

    // 4. FACTORY METHODS (Static Constructors)
    public static Quantity of(long value, UnitOfMeasure unit) {
        return new Quantity(value, unit);
    }

    public static Quantity zero(UnitOfMeasure unit) {
        return new Quantity(0, unit);
    }

    // 5. INTERNAL SAFETY CHECK
    private void checkUnitCompatibility(Quantity other) {
        Objects.requireNonNull(other, "Cannot compare with null quantity");
        if (this.unit != other.unit) {
            throw new IllegalArgumentException(
                String.format("Unit mismatch: Cannot mix %s with %s", this.unit, other.unit)
            );
        }
    }

    // 6. SORTING SUPPORT
    @Override
    public int compareTo(Quantity other) {
        checkUnitCompatibility(other);
        return Long.compare(this.value, other.value);
    }
}