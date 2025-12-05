package me.abdelaziz.main;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.feature.*;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.runtime.function.NativeFunction;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.token.Token;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public final class Main {

    public static void main(final String[] args) throws Exception {
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

        final String code = new String(Files.readAllBytes(Paths.get(args[0])));

        final Lexer lexer = new Lexer(code);
        final List<Token> tokens = lexer.tokenize();

        final Parser parser = new Parser(tokens);
        final List<Statement> program = parser.parse();

        final Environment globalEnv = new Environment(null);

        addSTDs(globalEnv);

        for (final Statement stmt : program)
            stmt.execute(globalEnv);
    }

    private static void addSTDs(final Environment globalEnv) {
        globalEnv.define("input", new Value(new NativeFunction((env, args) -> {
            if (!args.isEmpty())
                System.out.print(args.get(0));

            return new Value(new Scanner(System.in).nextLine());
        })), true);

        // 4. number(val) - converts a string (from input) into a number
        globalEnv.define("number", new Value(new NativeFunction((env, args) -> {
            if (args.isEmpty())
                throw new RuntimeException("Function 'number' requires 1 argument.");

            return new Value(args.get(0).asDouble());
        })), true);

        globalEnv.define("time", new Value(new NativeFunction((env, args) -> new Value((double) System.currentTimeMillis()))), true);

        globalEnv.define("random", new Value(new NativeFunction((env, args) -> {
            switch (args.size()) {
                case 0:
                    return new Value(Math.random());
                case 1:
                    return new Value((int) (Math.random() * (args.get(0).asDouble().intValue() + 1)));
                case 2:
                    final int min = args.get(0).asDouble().intValue(),
                            max = args.get(1).asDouble().intValue();

                    return new Value(min + (int) (Math.random() * (max - min + 1)));
            }

            throw new RuntimeException("Usage: random(), random(max), or random(min, max)");
        })), true);
    }
}