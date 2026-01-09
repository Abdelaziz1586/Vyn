package me.abdelaziz.main;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.bytecode.Chunk;
import me.abdelaziz.bytecode.OpCode;
import me.abdelaziz.compiler.BytecodeCompiler;
import me.abdelaziz.feature.*;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.clazz.nat.ListNativeClass;
import me.abdelaziz.runtime.clazz.nat.MapNativeClass;
import me.abdelaziz.runtime.function.nat.conversion.StringNativeFunction;
import me.abdelaziz.runtime.function.nat.net.AtNativeFunction;
import me.abdelaziz.runtime.function.nat.net.FetchNativeFunction;
import me.abdelaziz.runtime.function.nat.net.PackNativeFunction;
import me.abdelaziz.runtime.function.nat.net.UnpackNativeFunction;
import me.abdelaziz.runtime.function.nat.conversion.IntegerNativeFunction;
import me.abdelaziz.runtime.function.nat.conversion.NumberNativeFunction;

import me.abdelaziz.runtime.function.nat.system.RandomNativeFunction;
import me.abdelaziz.runtime.function.nat.system.*;
import me.abdelaziz.ast.statement.*;
import me.abdelaziz.util.Importer;
import me.abdelaziz.vm.VynVM;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public final class VynMain {

    private static Consumer<Environment> stdListener;

    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar vyn.jar [-c <script.vyn> -o <out.vyc>] [-d] | <file>");
            return;
        }

        String inputPath = null;
        String outputPath = null;
        boolean compileMode = false;
        boolean debugMode = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    compileMode = true;
                    if (i + 1 < args.length) inputPath = args[++i];
                    break;
                case "-o":
                    if (i + 1 < args.length) outputPath = args[++i];
                    break;
                case "-d":
                    debugMode = true;
                    break;
                default:
                    inputPath = args[i];
                    break;
            }
        }

        init(true);

        if (compileMode && inputPath != null && outputPath != null) {
            compileToFile(inputPath, outputPath, debugMode);
        } else if (inputPath != null) {
            if (inputPath.endsWith(".vyc")) {
                runBytecodeFile(inputPath);
            } else {
                loadFile(inputPath);
            }
        }
    }

    private static void compileToFile(final String input, final String output, final boolean debug) {
        try {
            final String source = new String(Files.readAllBytes(Paths.get(input)));
            final Lexer lexer = new Lexer(source);
            final Parser parser = new Parser(lexer.tokenize());
            final List<Statement> program = parser.parse();

            final BytecodeCompiler compiler = new BytecodeCompiler();
            final Chunk chunk = compiler.compile(program);

            if (debug) debugChunk(chunk);

            try (final DataOutputStream out = new DataOutputStream(Files.newOutputStream(Paths.get(output)))) {
                final List<Value> constants = chunk.constants;
                out.writeInt(constants.size());
                for (final Value v : constants) {
                    final Object obj = v.asJavaObject();
                    if (obj instanceof Double) {
                        out.writeByte(0);
                        out.writeDouble((Double) obj);
                    } else if (obj instanceof Long) {
                        out.writeByte(1);
                        out.writeLong((Long) obj);
                    } else if (obj instanceof String) {
                        out.writeByte(2);
                        out.writeUTF((String) obj);
                    } else if (obj == null) {
                        out.writeByte(3);
                    } else {
                        out.writeByte(4);
                        out.writeUTF(obj.toString());
                    }
                }

                final byte[] code = chunk.getRawCode();
                out.writeInt(code.length);
                out.write(code);
            }
            System.out.println("Compiled " + input + " to " + output);
        } catch (final Exception e) {
            System.err.println("Compilation failed: " + e.getMessage());
        }
    }

    private static void debugChunk(final Chunk chunk) {
        System.out.println("\n--- Bytecode Debug ---");
        final List<Byte> code = chunk.code;
        for (int i = 0; i < code.size(); i++) {
            final int opIdx = code.get(i) & 0xFF;
            if (opIdx >= OpCode.values().length) {
                System.out.printf("%04d: UNKNOWN (%d)%n", i, opIdx);
                continue;
            }
            final OpCode op = OpCode.values()[opIdx];
            System.out.printf("%04d: %-15s", i, op.name());

            switch (op) {
                case CONSTANT: {
                    final int idx = code.get(++i) & 0xFF;
                    System.out.print(" " + idx + " (" + chunk.constants.get(idx) + ")");
                    break;
                }
                case JUMP:
                case JUMP_IF_FALSE:
                case LOOP: {
                    final int msb = code.get(++i) & 0xFF;
                    final int lsb = code.get(++i) & 0xFF;
                    System.out.print(" " + ((msb << 8) | lsb));
                    break;
                }
                case CLOSURE: {
                    final int msb = code.get(++i) & 0xFF;
                    final int lsb = code.get(++i) & 0xFF;
                    final int arity = code.get(++i) & 0xFF;
                    System.out.print(" addr: " + ((msb << 8) | lsb) + " arity: " + arity);
                    break;
                }
                case CALL:
                case ARRAY:
                case CATALOG: {
                    System.out.print(" " + (code.get(++i) & 0xFF));
                    break;
                }
                case SPLIT: {
                    final int msb = code.get(++i) & 0xFF;
                    final int lsb = code.get(++i) & 0xFF;
                    System.out.print(" addr: " + ((msb << 8) | lsb));
                    break;
                }
                default: break;
            }
            System.out.println();
        }
        System.out.println("--- End Debug ---\n");
    }

    private static void runBytecodeFile(final String path) {
        try (final DataInputStream in = new DataInputStream(Files.newInputStream(Paths.get(path)))) {
            final Chunk chunk = new Chunk();

            final int constantCount = in.readInt();
            for (int i = 0; i < constantCount; i++) {
                final int type = in.readByte();
                switch (type) {
                    case 0:
                        chunk.addConstant(new Value(in.readDouble()));
                        break;
                    case 1:
                        chunk.addConstant(new Value(in.readLong()));
                        break;
                    case 2:
                    case 4:
                        chunk.addConstant(new Value(in.readUTF()));
                        break;
                    case 3:
                        chunk.addConstant(new Value(null));
                        break;
                }
            }

            final int codeLength = in.readInt();
            final byte[] code = new byte[codeLength];
            in.readFully(code);
            for (final byte b : code) chunk.write(b & 0xFF);

            final VynVM vm = new VynVM();
            vm.interpret(chunk);
        } catch (final Exception e) {
            System.err.println("VM Error: " + e.getMessage());
        }
    }

    public static void init(final boolean addShutdownHook) {
        if (addShutdownHook)
            Runtime.getRuntime().addShutdownHook(new Thread(Importer::unloadAll));

        Parser.register("make", new MakeHandler());
        Parser.register("lock", new LockHandler());
        Parser.register("say", parser -> new PrintStatement(parser.expression(), true));
        Parser.register("write", parser -> new PrintStatement(parser.expression(), false));
        Parser.register("check", new CheckHandler());
        Parser.register("blueprint", new BlueprintHandler());
        Parser.register("catalog", new CatalogHandler());
        Parser.register("cycle", new CycleHandler());
        Parser.register("task", new TaskHandler());
        Parser.register("reply", parser -> new ReturnStatement(parser.expression()));
        Parser.register("use", new UseHandler());
        Parser.register("attempt", new AttemptHandler());
        Parser.register("escape", parser -> new BreakStatement());
        Parser.register("skip", parser -> new ContinueStatement());
        Parser.register("hold", parser -> new HoldStatement(parser.expression()));
        Parser.register("split", new SplitHandler());
        Parser.register("build", new BuildHandler());
        Parser.register("demolish", new DemolishHandler());
    }

    public static void loadFile(final String fileName) {
        final Environment globalEnv = new Environment(null);
        addSTDs(globalEnv);
        Importer.load(fileName, globalEnv);
    }

    @SuppressWarnings("unused")
    public static void setStdListener(final Consumer<Environment> stdListener) {
        VynMain.stdListener = stdListener;
    }

    public static void addSTDs(final Environment globalEnv) {
        globalEnv.defineFunction("sort", new SortNativeFunction());
        globalEnv.defineFunction("time", new TimeNativeFunction());
        globalEnv.defineFunction("size", new SizeNativeFunction());
        globalEnv.defineFunction("input", new InputNativeFunction());
        globalEnv.defineFunction("random", new RandomNativeFunction());
        globalEnv.defineFunction("discard", new DiscardNativeFunction());
        globalEnv.defineFunction("min", new MinMaxNativeFunction(true));
        globalEnv.defineFunction("max", new MinMaxNativeFunction(false));
        globalEnv.defineFunction("int", new IntegerNativeFunction());
        globalEnv.defineFunction("number", new NumberNativeFunction());
        globalEnv.defineFunction("string", new StringNativeFunction());
        globalEnv.defineFunction("at", new AtNativeFunction());
        globalEnv.defineFunction("pack", new PackNativeFunction());
        globalEnv.defineFunction("fetch", new FetchNativeFunction());
        globalEnv.defineFunction("unpack", new UnpackNativeFunction());
        globalEnv.defineClass("Map", new MapNativeClass());
        globalEnv.defineClass("List", new ListNativeClass());

        if (stdListener != null)
            stdListener.accept(globalEnv);
    }
}