package me.abdelaziz.runtime.function.nat.math;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

public final class TrigNativeFunction extends NativeFunction {

    public TrigNativeFunction(final TrigType type) {
        super((env, args) -> {
            if (args.size() != 1)
                throw new RuntimeException("Function '" + type.getName() + "' requires 1 argument.");

            return getValue(args.get(0), type);
        });
    }

    private static Value getValue(final Value value, final TrigType type) {
        final double v = value.asDouble();

        switch (type) {
            case SIN:
                return new Value(Math.sin(v));
            case COS:
                return new Value(Math.cos(v));
            case TAN:
                return new Value(Math.tan(v));

            case ASIN:
                return new Value(Math.asin(v));
            case ACOS:
                return new Value(Math.acos(v));
            case ATAN:
                return new Value(Math.atan(v));

            case SINH:
                return new Value(Math.sinh(v));
            case COSH:
                return new Value(Math.cosh(v));
            case TANH:
                return new Value(Math.tanh(v));

            case SEC:
                return new Value(1.0 / Math.cos(v));
            case CSC:
                return new Value(1.0 / Math.sin(v));
            case COT:
                return new Value(1.0 / Math.tan(v));

            default:
                throw new IllegalStateException("Unhandled trig type: " + type);
        }
    }

    public enum TrigType {
        SIN("sin"),
        COS("cos"),
        TAN("tan"),

        ASIN("asin"),
        ACOS("acos"),
        ATAN("atan"),

        SINH("sinh"),
        COSH("cosh"),
        TANH("tanh"),

        SEC("sec"),
        CSC("csc"),
        COT("cot");

        private final String name;

        TrigType(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}