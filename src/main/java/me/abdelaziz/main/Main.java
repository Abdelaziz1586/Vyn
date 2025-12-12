package me.abdelaziz.main;

import me.abdelaziz.feature.*;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.function.nat.conversion.StringNativeFunction;
import me.abdelaziz.runtime.function.nat.net.AtNativeFunction;
import me.abdelaziz.runtime.function.nat.net.FetchNativeFunction;
import me.abdelaziz.runtime.function.nat.net.PackNativeFunction;
import me.abdelaziz.runtime.function.nat.net.UnpackNativeFunction;
import me.abdelaziz.runtime.function.nat.conversion.IntegerNativeFunction;
import me.abdelaziz.runtime.function.nat.conversion.NumberNativeFunction;
import me.abdelaziz.runtime.function.nat.system.RandomNativeFunction;
import me.abdelaziz.runtime.function.nat.system.*;
import me.abdelaziz.util.Importer;

public final class Main {

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar botify.jar <script.bt>");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(Importer::unloadAll));

        Parser.register("make", new MakeHandler());
        Parser.register("lock", new LockHandler());
        Parser.register("say", new OutputHandler(true));
        Parser.register("write", new OutputHandler(false));
        Parser.register("check", new CheckHandler());
        Parser.register("blueprint", new BlueprintHandler());
        Parser.register("cycle", new CycleHandler());
        Parser.register("task", new TaskHandler());
        Parser.register("reply", new ReplyHandler());
        Parser.register("use", new UseHandler());
        Parser.register("attempt", new AttemptHandler());
        Parser.register("escape", new EscapeHandler());
        Parser.register("skip", new SkipHandler());
        Parser.register("hold", new HoldHandler());
        Parser.register("split", new SplitHandler());
        Parser.register("build", new BuildHandler());
        Parser.register("demolish", new DemolishHandler());

        final Environment globalEnv = new Environment(null);
        addSTDs(globalEnv);

        Importer.load(args[0], globalEnv);
    }

    private static void addSTDs(final Environment globalEnv) {
        globalEnv.defineFunction("sort", new SortNativeFunction());
        globalEnv.defineFunction("time", new TimeNativeFunction());
        globalEnv.defineFunction("size", new SizeNativeFunction());
        globalEnv.defineFunction("input", new InputNativeFunction());
        globalEnv.defineFunction("random", new RandomNativeFunction());
        globalEnv.defineFunction("discard", new DiscardNativeFunction());

        globalEnv.defineFunction("int", new IntegerNativeFunction());
        globalEnv.defineFunction("number", new NumberNativeFunction());        globalEnv.defineFunction("number", new NumberNativeFunction());
        globalEnv.defineFunction("string", new StringNativeFunction());

        globalEnv.defineFunction("at", new AtNativeFunction());
        globalEnv.defineFunction("pack", new PackNativeFunction());
        globalEnv.defineFunction("fetch", new FetchNativeFunction());
        globalEnv.defineFunction("unpack", new UnpackNativeFunction());
    }
}