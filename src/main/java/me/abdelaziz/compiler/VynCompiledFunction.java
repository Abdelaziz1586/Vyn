package me.abdelaziz.compiler;

public final class VynCompiledFunction {

    public final int arity;
    public final int address;
    public final String[] parameters;

    public VynCompiledFunction(final int address, final int arity, final String[] parameters) {
        this.address = address;
        this.arity = arity;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "<task at " + address + ">";
    }
}