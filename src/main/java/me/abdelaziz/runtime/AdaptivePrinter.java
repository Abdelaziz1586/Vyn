package me.abdelaziz.runtime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public final class AdaptivePrinter {

    private static long lastTime = System.nanoTime();
    private static final boolean isTerminal = System.console() != null;
    private static final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    private static int counter = 0;
    private static int printsThisSecond = 0;
    private static final int BATCH_SIZE = 500;
    private static final int HIGH_RATE_THRESHOLD = 1000;

    private AdaptivePrinter() {
    }

    public static void print(final Object obj) {
        try {
            writer.write(String.valueOf(obj));
            writer.write("\n");
            counter++;
            printsThisSecond++;

            final long now = System.nanoTime();
            if ((now - lastTime) / 1_000_000_000.0 >= 1.0) {
                printsThisSecond = 0;
                lastTime = now;
            }

            if (isTerminal) {
                if (printsThisSecond < HIGH_RATE_THRESHOLD) {
                    writer.flush();
                    counter = 0;
                    return;
                }

                if (counter >= BATCH_SIZE) {
                    writer.flush();
                    counter = 0;
                }

                return;
            }

            if (counter >= BATCH_SIZE) {
                writer.flush();
                counter = 0;
            }

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void flush() {
        try {
            writer.flush();
            counter = 0;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            writer.flush();
            writer.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
