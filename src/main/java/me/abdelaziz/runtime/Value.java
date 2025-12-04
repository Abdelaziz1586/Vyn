package me.abdelaziz.runtime;

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
    public String toString() {
        if (value instanceof Number) {
            final double d = ((Number) value).doubleValue();
            if (d == (long) d)
                return String.valueOf((long) d);
        }

        return String.valueOf(value);
    }
}