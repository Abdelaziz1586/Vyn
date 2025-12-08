package me.abdelaziz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class ProjectScanner {

    public static void main(final String[] args) {
        System.out.println("--- Starting Project Scan ---");

        final Path mainPath = Paths.get("src", "main", "java");

        processDirectory(mainPath);

        System.out.println("\n--- Project Scan Complete ---");
    }

    private static void processDirectory(final Path startPath) {
        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            System.out.println("\nDirectory not found, skipping: " + startPath);
            return;
        }

        System.out.println("\nScanning Directory: " + startPath);
        System.out.println("========================================");

        try (final Stream<Path> paths = Files.walk(startPath)) {
            paths.filter(Files::isRegularFile).forEach(ProjectScanner::printFileContent);
        } catch (final IOException e) {
            System.err.println("Error walking directory " + startPath + ": " + e.getMessage());
        }
    }

    private static void printFileContent(final Path filePath) {
        try {
            final String fileName = filePath.getFileName().toString();
            final String content = String.join("\n", Files.readAllLines(filePath));

            System.out.println("\n" + fileName);
            System.out.println("// --- content of " + fileName + " ---");
            System.out.println(content);
            System.out.println("// --- end of " + fileName + " ---");

        } catch (final IOException e) {
            System.err.println("Could not read file " + filePath + ": " + e.getMessage());
        }
    }
}