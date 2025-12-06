package me.abdelaziz.util;

import me.abdelaziz.ast.Statement;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Importer {

    private static final Set<String> loadedFiles = new HashSet<>();

    public static void load(final String filePath, final Environment env) {
        try {
            final File file = new File(filePath);
            final String absPath = file.getCanonicalPath();

            if (loadedFiles.contains(absPath))
                return;

            if (!file.exists())
                throw new RuntimeException("File not found: " + filePath);

            loadedFiles.add(absPath);

            final List<Statement> program = new Parser(new Lexer(new String(Files.readAllBytes(file.toPath()))).tokenize()).parse();
            for (final Statement stmt : program)
                stmt.execute(env);

        } catch (final Exception e) {
            throw new RuntimeException("Error loading '" + filePath + "': " + e.getMessage());
        }
    }
}