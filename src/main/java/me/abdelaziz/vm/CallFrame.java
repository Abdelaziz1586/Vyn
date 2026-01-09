package me.abdelaziz.vm;

import me.abdelaziz.bytecode.Chunk;

public final class CallFrame {

    public int ip;
    public final int slots;
    public final Chunk chunk;

    public CallFrame(final Chunk chunk, final int ip, final int slots) {
        this.ip = ip;
        this.chunk = chunk;
        this.slots = slots;
    }
}