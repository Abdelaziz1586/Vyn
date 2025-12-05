package me.abdelaziz.runtime;

import java.util.Objects;

public final class Value {

    private final Object value;

    public Value(final Object value) {
        this.value = value;
    }

    public Object asJavaObject() {
        return value;
    }

    public Double asDouble() {
        if (value instanceof Number)
            return ((Number) value).doubleValue();

        try {
            return Double.parseDouble(value.toString());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Cannot convert '" + value + "' to a number.");
        }
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Value other = (Value) o;

        if (this.value instanceof Number && other.value instanceof Number)
            return ((Number) this.value).doubleValue() == ((Number) other.value).doubleValue();

        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        if (value instanceof Number)
            return Objects.hash(((Number) value).doubleValue());

        return Objects.hash(value);
    }

    @Override
    public String toString() {
        if (value instanceof Number) {
            final double d = ((Number) value).doubleValue();
            if (d == (long) d)
                return String.valueOf((long) d);
        }

        return String.valueOf(value);
    }
}