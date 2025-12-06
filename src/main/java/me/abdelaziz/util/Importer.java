package me.abdelaziz.util;

import me.abdelaziz.api.BotifyLibrary;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Importer {

    private static final Set<String> loadedFiles = new HashSet<>();
    private static ClassLoader libraryLoader;

    public static void load(final String path, final Environment env) {
        try {
            if (path.startsWith("java:")) {
                final String className = path.substring(5);

                if (loadedFiles.contains(className))
                    return;

                if (libraryLoader == null)
                    initLibraryLoader();

                final Class<?> clazz = Class.forName(className, true, libraryLoader);

                if (!BotifyLibrary.class.isAssignableFrom(clazz))
                    throw new RuntimeException("Class " + className + " does not implement BotifyLibrary.");

                final BotifyLibrary lib = (BotifyLibrary) clazz.getDeclaredConstructor().newInstance();
                lib.onEnable(env);

                loadedFiles.add(className);
                return;
            }

            final File file = new File(path);
            final String absPath = file.getCanonicalPath();

            if (loadedFiles.contains(absPath))
                return;

            if (!file.exists())
                throw new RuntimeException("File not found: " + path);

            loadedFiles.add(absPath);

            final String code = new String(Files.readAllBytes(file.toPath()));

            final Lexer lexer = new Lexer(code);
            final Parser parser = new Parser(lexer.tokenize());
            final List<Statement> program = parser.parse();

            for (final Statement stmt : program)
                stmt.execute(env);
        } catch (final Exception e) {
            throw new RuntimeException("Error loading '" + path + "': " + e.getMessage());
        }
    }

    private static void initLibraryLoader() {
        try {
            final File libsDir = new File("libs");

            if (!libsDir.exists() || !libsDir.isDirectory()) {
                libraryLoader = ClassLoader.getSystemClassLoader();
                return;
            }

            File[] jars = libsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jars == null)
                jars = new File[0];

            final URL[] urls = new URL[jars.length];
            for (int i = 0; i < jars.length; i++)
                urls[i] = jars[i].toURI().toURL();

            libraryLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        } catch (final Exception e) {
            throw new RuntimeException("Failed to initialize library loader: " + e.getMessage());
        }
    }
}
