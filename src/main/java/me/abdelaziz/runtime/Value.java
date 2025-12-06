package me.abdelaziz.runtime;

import java.util.Objects;

public final class Value {

    private Object object;
    private double number;
    private boolean isNumber;

    public Value(final Object value) {
        if (value instanceof Number) {
            this.number = ((Number) value).doubleValue();
            this.isNumber = true;
            this.object = null;
        } else {
            this.object = value;
            this.isNumber = false;
        }
    }

    public Value(final double value) {
        this.number = value;
        this.isNumber = true;
        this.object = null;
    }

    public void set(final double value) {
        this.number = value;
        this.isNumber = true;
        this.object = null;
    }

    public Object asJavaObject() {
        return isNumber ? number : object;
    }

    public int asInt() {
        return isNumber ? (int) number : Integer.parseInt(object.toString());
    }

    public Double asDouble() {
        if (isNumber) return number;

        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert '" + object + "' to a number.");
        }
    }

    public Boolean asBoolean() {
        if (isNumber) return number != 0;
        return (Boolean) object;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Value other = (Value) o;

        if (this.isNumber && other.isNumber)
            return this.number == other.number;

        final Object v1 = this.isNumber ? this.number : this.object;
        final Object v2 = other.isNumber ? other.number : other.object;

        if (v1 == null && v2 == null) return true;
        if (v1 == null || v2 == null) return false;

        return v1.toString().equals(v2.toString());
    }

    @Override
    public int hashCode() {
        return isNumber
                ? Double.hashCode(number)
                : Objects.hashCode(object);
    }

    @Override
    public String toString() {
        if (isNumber) {
            if (number == (long) number)
                return String.valueOf((long) number);

            return String.valueOf(number);
        }

        return object == null
                ? "nothing"
                : object.toString();
    }
}