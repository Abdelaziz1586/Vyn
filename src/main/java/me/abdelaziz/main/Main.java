package me.abdelaziz.main;

import me.abdelaziz.feature.*;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.function.nat.*;
import me.abdelaziz.util.Importer;

public final class Main {

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar botify.jar <script.bt>");
            return;
        }

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
        Parser.register("build", new BuildHandler());

        final Environment globalEnv = new Environment(null);
        addSTDs(globalEnv);

        Importer.load(args[0], globalEnv);
    }

    private static void addSTDs(final Environment globalEnv) {
        globalEnv.defineFunction("input", new InputNativeFunction());
        globalEnv.defineFunction("number", new NumberNativeFunction());
        globalEnv.defineFunction("time", new TimeNativeFunction());
        globalEnv.defineFunction("random", new RandomNativeFunction());
        globalEnv.defineFunction("fetch", new FetchNativeFunction());
        globalEnv.defineFunction("unpack", new UnpackNativeFunction());
        globalEnv.defineFunction("pack", new PackNativeFunction());
        globalEnv.defineFunction("at", new AtNativeFunction());
        globalEnv.defineFunction("size", new SizeNativeFunction());
        globalEnv.defineFunction("sort", new SortNativeFunction());
    }
}