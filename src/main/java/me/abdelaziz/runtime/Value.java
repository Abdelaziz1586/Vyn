package me.abdelaziz.runtime;

import java.util.Objects;

public final class Value {

    private Object object;
    private double number;
    private long integer;
    private boolean isNumber;
    private boolean isInteger;

    public Value(final Object value) {
        if (value instanceof Number) {
            final Number n = (Number) value;
            if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) {
                this.integer = n.longValue();
                this.number = n.doubleValue();
                this.isInteger = true;
            } else {
                this.number = n.doubleValue();
                this.isInteger = false;
            }
            this.isNumber = true;
            this.object = null;
        } else {
            this.object = value;
            this.isNumber = false;
            this.isInteger = false;
        }
    }

    public Value(final double value) {
        this.number = value;
        this.integer = (long) value;
        this.isNumber = true;
        this.isInteger = value == (double) this.integer;
        this.object = null;
    }

    public Value(final long value) {
        this.number = (double) value;
        this.integer = value;
        this.isNumber = true;
        this.isInteger = true;
        this.object = null;
    }

    public void set(final double value) {
        this.number = value;
        this.integer = (long) value;
        this.isNumber = true;
        this.isInteger = value == (double) this.integer;
        this.object = null;
    }

    public void set(final long value) {
        this.number = (double) value;
        this.integer = value;
        this.isNumber = true;
        this.isInteger = true;
        this.object = null;
    }

    public Object asJavaObject() {
        return isNumber ? (isInteger ? integer : number) : object;
    }

    public int asInt() {
        return isNumber ? (isInteger ? (int) integer : (int) number) : Integer.parseInt(object.toString());
    }

    public long asLong() {
        return isNumber ? (isInteger ? integer : (long) number) : Long.parseLong(object.toString());
    }

    public Double asDouble() {
        if (isNumber)
            return number;

        try {
            return Double.parseDouble(object.toString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert '" + object + "' to a number.");
        }
    }

    public Boolean asBoolean() {
        if (isNumber)
            return number != 0;
        return (Boolean) object;
    }

    public boolean isInteger() {
        return isInteger;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Value other = (Value) o;

        if (this.isNumber && other.isNumber) {
            if (this.isInteger && other.isInteger)
                return this.integer == other.integer;
            return this.number == other.number;
        }

        final Object v1 = this.isNumber ? (isInteger ? integer : number) : this.object;
        final Object v2 = other.isNumber ? (other.isInteger ? other.integer : other.number) : other.object;

        if (v1 == null && v2 == null)
            return true;
        if (v1 == null || v2 == null)
            return false;

        return v1.toString().equals(v2.toString());
    }

    @Override
    public int hashCode() {
        if (isNumber) {
            return isInteger ? Long.hashCode(integer) : Double.hashCode(number);
        }
        return Objects.hashCode(object);
    }

    @Override
    public String toString() {
        if (isNumber) {
            if (isInteger || number == (long) number)
                return String.valueOf(isInteger ? integer : (long) number);
            return String.valueOf(number);
        }

        return object == null ? "nothing" : object.toString();
    }
}