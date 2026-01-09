package me.abdelaziz.bytecode;

import me.abdelaziz.runtime.Value;
import java.util.ArrayList;
import java.util.List;

public final class Chunk {

    public final List<Byte> code = new ArrayList<>();
    public final List<Value> constants = new ArrayList<>();

    public void write(int b) {
        code.add((byte) b);
    }

    public void write(OpCode op) {
        code.add((byte) op.ordinal());
    }

    public int addConstant(Value value) {
        constants.add(value);
        return constants.size() - 1;
    }

    public byte[] getRawCode() {
        byte[] raw = new byte[code.size()];
        for (int i = 0; i < code.size(); i++) raw[i] = code.get(i);
        return raw;
    }
}