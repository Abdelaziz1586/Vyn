package me.abdelaziz.main;

import me.abdelaziz.feature.*;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
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

import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class VynMain {

    private static Consumer<Environment> stdListener;

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar vyn.jar <script.vyn>");
            return;
        }

        init(true);

        loadFile(args[0]);
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

    public static Environment loadLines(final String code) {
        final Environment globalEnv = new Environment(null);
        addSTDs(globalEnv);

        Importer.loadFromLines(code, globalEnv);

        return globalEnv;
    }

    public static void setStdListener(final Consumer<Environment> stdListener) {
        VynMain.stdListener = stdListener;
    }

    private static void addSTDs(final Environment globalEnv) {
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