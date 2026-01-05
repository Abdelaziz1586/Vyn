package me.abdelaziz.util;

import me.abdelaziz.api.VynLibrary;
import me.abdelaziz.api.annotation.VynType;
import me.abdelaziz.ast.Statement;
import me.abdelaziz.lexer.Lexer;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.*;

public final class Importer {

    private static ClassLoader libraryLoader;
    private static final Set<String> loadedFiles = new HashSet<>();
    private static final List<VynLibrary> loadedLibraries = new ArrayList<>();

    public static void load(final String path, final Environment env) {
        try {
            if (path.startsWith("java:")) {
                final String className = path.substring(5);
                if (loadedFiles.contains(className))
                    return;

                if (libraryLoader == null)
                    initLibraryLoader();
                final Class<?> clazz = Class.forName(className, true, libraryLoader);

                if (clazz.isAnnotationPresent(VynType.class)) {
                    NativeBinder.bind(env, clazz);
                    loadedFiles.add(className);
                    return;
                }

                if (VynLibrary.class.isAssignableFrom(clazz)) {
                    final VynLibrary lib = (VynLibrary) clazz.getDeclaredConstructor().newInstance();
                    lib.onEnable(env);
                    loadedLibraries.add(lib);
                    loadedFiles.add(className);
                    return;
                }

                throw new RuntimeException("Class " + className + " invalid library type.");
            }

            final File file = new File(path);
            final String absPath = file.getCanonicalPath();
            if (loadedFiles.contains(absPath))
                return;
            if (!file.exists())
                throw new RuntimeException("File not found: " + path);

            loadedFiles.add(absPath);
            loadFromLines(new String(Files.readAllBytes(file.toPath())), env);
        } catch (final Exception e) {
            final String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new RuntimeException("Error loading '" + path + "': " + msg);
        }
    }

    public static void loadFromLines(final String code, final Environment env) {
        final Lexer lexer = new Lexer(code);
        final Parser parser = new Parser(lexer.tokenize());
        final List<Statement> program = parser.parse();

        for (final Statement stmt : program)
            stmt.execute(env);
    }

    public static void unloadAll() {
        for (final VynLibrary lib : loadedLibraries) {
            try {
                lib.onDisable();
            } catch (final Exception e) {
                System.err.println("Error disabling library: " + e.getMessage());
            }
        }
        loadedLibraries.clear();
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