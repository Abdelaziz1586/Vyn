package me.abdelaziz.main;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.feature.*;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.token.Token;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        for (final Statement stmt : program)
            stmt.execute(globalEnv);
    }
}