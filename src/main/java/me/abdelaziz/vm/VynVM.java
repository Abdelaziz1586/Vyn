package me.abdelaziz.vm;

import me.abdelaziz.bytecode.Chunk;
import me.abdelaziz.bytecode.OpCode;
import me.abdelaziz.compiler.VynCompiledFunction;
import me.abdelaziz.main.VynMain;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.VynInstance;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.clazz.VynCatalog;
import me.abdelaziz.runtime.clazz.nat.NativeClass;
import me.abdelaziz.runtime.function.VynCallable;

import java.util.*;

public final class VynVM {

    private static final OpCode[] OP_CODES = OpCode.values();
    private final CallFrame[] frames = new CallFrame[256];
    private int frameCount = 0;
    private final Value[] stack = new Value[8192];
    private int stackTop = 0;
    private final Map<String, Value> globals = new HashMap<>(1024);

    public VynVM() {
        final Environment bridge = new Environment(null) {
            @Override
            public void define(final String name, final Value value, final boolean isConstant) {
                globals.put(name, value);
            }
            @Override
            public void defineFunction(final String name, final VynCallable function) {
                globals.put(name, new Value(function));
            }
            @Override
            public void defineClass(final String name, final NativeClass nativeClass) {
                super.defineClass(name, nativeClass);
            }
        };
        VynMain.addSTDs(bridge);
    }

    public void interpret(final Chunk chunk) {
        frames[frameCount++] = new CallFrame(chunk, 0, 0);
        run();
    }

    private void run() {
        while (frameCount > 0) {
            final CallFrame frame = frames[frameCount - 1];
            final byte[] code = frame.chunk.getRawCode();
            final List<Value> constants = frame.chunk.constants;

            while (frame.ip < code.length) {
                final int opCodeIdx = code[frame.ip++] & 0xFF;
                final OpCode op = OP_CODES[opCodeIdx];

                switch (op) {
                    case CONSTANT:
                        stack[stackTop++] = constants.get(code[frame.ip++] & 0xFF);
                        break;
                    case NULL:
                        stack[stackTop++] = new Value(null);
                        break;
                    case TRUE:
                        stack[stackTop++] = new Value(true);
                        break;
                    case FALSE:
                        stack[stackTop++] = new Value(false);
                        break;
                    case POP:
                        stackTop--;
                        break;
                    case GET_GLOBAL: {
                        final String name = stack[--stackTop].toString();
                        final Value val = globals.get(name);
                        if (val == null) throw new RuntimeException("Undefined: " + name);
                        stack[stackTop++] = val;
                        break;
                    }
                    case DEFINE_GLOBAL:
                    case SET_GLOBAL: {
                        final String name = stack[--stackTop].toString();
                        globals.put(name, stack[--stackTop]);
                        break;
                    }
                    case GET_PROPERTY: {
                        final String name = stack[--stackTop].toString();
                        final Value obj = stack[--stackTop];
                        final Object raw = obj.asJavaObject();
                        if (raw instanceof VynInstance) stack[stackTop++] = ((VynInstance) raw).get(name);
                        else if (raw instanceof VynCatalog) stack[stackTop++] = ((VynCatalog) raw).get(name);
                        else throw new RuntimeException("Invalid property access");
                        break;
                    }
                    case SET_PROPERTY: {
                        final String name = stack[--stackTop].toString();
                        final Value val = stack[--stackTop];
                        final Value obj = stack[--stackTop];
                        final Object raw = obj.asJavaObject();
                        if (raw instanceof VynInstance) ((VynInstance) raw).set(name, val);
                        break;
                    }
                    case GET_INDEX: {
                        final Value idx = stack[--stackTop];
                        final Value obj = stack[--stackTop];
                        final Object raw = obj.asJavaObject();
                        if (raw instanceof List) stack[stackTop++] = (Value) ((List<?>) raw).get(idx.asInt());
                        else if (raw instanceof String) stack[stackTop++] = new Value(String.valueOf(raw.toString().charAt(idx.asInt())));
                        break;
                    }
                    case SET_INDEX: {
                        final Value val = stack[--stackTop];
                        final Value idx = stack[--stackTop];
                        final Value obj = stack[--stackTop];
                        final Object raw = obj.asJavaObject();
                        if (raw instanceof List) ((List<Value>) raw).set(idx.asInt(), val);
                        break;
                    }
                    case ADD: {
                        final Value b = stack[--stackTop];
                        final Value a = stack[--stackTop];
                        final Object aObj = a.asJavaObject();
                        final Object bObj = b.asJavaObject();
                        if (aObj instanceof String || bObj instanceof String) {
                            stack[stackTop++] = new Value(a.toString() + b);
                        } else {
                            stack[stackTop++] = new Value(a.asDouble() + b.asDouble());
                        }
                        break;
                    }
                    case SUBTRACT: {
                        final double b = stack[--stackTop].asDouble();
                        final double a = stack[--stackTop].asDouble();
                        stack[stackTop++] = new Value(a - b);
                        break;
                    }
                    case MULTIPLY: {
                        final double b = stack[--stackTop].asDouble();
                        final double a = stack[--stackTop].asDouble();
                        stack[stackTop++] = new Value(a * b);
                        break;
                    }
                    case DIVIDE: {
                        final double b = stack[--stackTop].asDouble();
                        final double a = stack[--stackTop].asDouble();
                        stack[stackTop++] = new Value(a / b);
                        break;
                    }
                    case MODULO: {
                        final double b = stack[--stackTop].asDouble();
                        final double a = stack[--stackTop].asDouble();
                        stack[stackTop++] = new Value(a % b);
                        break;
                    }
                    case EQUAL:
                        stack[stackTop - 2] = new Value(stack[stackTop - 2].equals(stack[--stackTop]));
                        break;
                    case GREATER: {
                        final double b = stack[--stackTop].asDouble();
                        final double a = stack[--stackTop].asDouble();
                        stack[stackTop++] = new Value(a > b);
                        break;
                    }
                    case LESS: {
                        final double b = stack[--stackTop].asDouble();
                        final double a = stack[--stackTop].asDouble();
                        stack[stackTop++] = new Value(a < b);
                        break;
                    }
                    case NOT:
                        stack[stackTop - 1] = new Value(!stack[stackTop - 1].asBoolean());
                        break;
                    case NEGATE:
                        stack[stackTop - 1] = new Value(-stack[stackTop - 1].asDouble());
                        break;
                    case JUMP:
                        frame.ip = ((code[frame.ip] & 0xFF) << 8) | (code[frame.ip + 1] & 0xFF);
                        break;
                    case JUMP_IF_FALSE: {
                        final int addr = ((code[frame.ip] & 0xFF) << 8) | (code[frame.ip + 1] & 0xFF);
                        frame.ip += 2;
                        if (!stack[stackTop - 1].asBoolean()) frame.ip = addr;
                        break;
                    }
                    case LOOP:
                        frame.ip = ((code[frame.ip] & 0xFF) << 8) | (code[frame.ip + 1] & 0xFF);
                        break;
                    case PRINT:
                        System.out.println(stack[--stackTop]);
                        break;
                    case PRINT_NOLINE:
                        System.out.print(stack[--stackTop]);
                        break;
                    case CLOSURE: {
                        final int addr = ((code[frame.ip++] & 0xFF) << 8) | (code[frame.ip++] & 0xFF);
                        final int arity = code[frame.ip++] & 0xFF;
                        final String[] params = new String[arity];
                        for (int i = 0; i < arity; i++) {
                            params[i] = constants.get(code[frame.ip++] & 0xFF).toString();
                        }
                        stack[stackTop++] = new Value(new VynCompiledFunction(addr, arity, params));
                        break;
                    }
                    case CALL:
                        executeCall(code[frame.ip++] & 0xFF);
                        break;
                    case RETURN: {
                        final Value res = stack[--stackTop];
                        frameCount--;
                        if (frameCount > 0) stack[stackTop++] = res;
                        return;
                    }
                    case ARRAY: {
                        final int len = code[frame.ip++] & 0xFF;
                        final List<Value> list = new ArrayList<>(len);
                        final int start = stackTop - len;
                        list.addAll(Arrays.asList(stack).subList(start, len + start));
                        stackTop -= len;
                        stack[stackTop++] = new Value(list);
                        break;
                    }
                    case CATALOG: {
                        final int len = code[frame.ip++] & 0xFF;
                        final Map<String, Value> map = new LinkedHashMap<>(len);
                        for (int i = 0; i < len; i++) {
                            final String entry = stack[--stackTop].toString();
                            map.put(entry, new Value(entry));
                        }
                        stack[stackTop++] = new Value(new VynCatalog("catalog", map));
                        break;
                    }
                    case HOLD:
                        try { Thread.sleep(stack[--stackTop].asLong()); } catch (Exception ignored) {}
                        break;
                    case SPLIT: {
                        final int addr = ((code[frame.ip++] & 0xFF) << 8) | (code[frame.ip++] & 0xFF);
                        final Chunk c = frame.chunk;
                        new Thread(() -> {
                            final VynVM vm = new VynVM();
                            vm.globals.putAll(this.globals);
                            vm.frames[vm.frameCount++] = new CallFrame(c, addr, 0);
                            vm.run();
                        }).start();
                        break;
                    }
                    case HALT:
                        frameCount = 0;
                        return;
                    default:
                        throw new RuntimeException("OpCode not implemented: " + op);
                }
            }
            frameCount--;
        }
    }

    private void executeCall(final int argCount) {
        final Value callee = stack[stackTop - argCount - 1];
        final Object raw = callee.asJavaObject();

        if (raw instanceof VynCompiledFunction) {
            final VynCompiledFunction func = (VynCompiledFunction) raw;
            if (argCount != func.arity) throw new RuntimeException("Expected " + func.arity + " args");

            final int argBase = stackTop - argCount;
            for (int i = 0; i < argCount; i++) {
                globals.put(func.parameters[i], stack[argBase + i]);
            }

            stackTop -= (argCount + 1);
            frames[frameCount++] = new CallFrame(frames[frameCount - 2].chunk, func.address, stackTop);
            run();
        } else if (raw instanceof VynCallable) {
            final List<Value> args = new ArrayList<>(argCount);
            final int argBase = stackTop - argCount;
            args.addAll(Arrays.asList(stack).subList(argBase, argCount + argBase));
            stackTop -= (argCount + 1);
            stack[stackTop++] = ((VynCallable) raw).call(null, args);
        } else {
            throw new RuntimeException("Not callable: " + raw);
        }
    }
}