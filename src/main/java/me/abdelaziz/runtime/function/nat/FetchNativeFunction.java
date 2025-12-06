package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.util.SimpleJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class FetchNativeFunction extends NativeFunction {

    @SuppressWarnings("unchecked")
    public FetchNativeFunction() {
        super((env, args) -> {
            try {
                if (args.isEmpty())
                    throw new RuntimeException("Usage: fetch(url) or fetch(url, config)");

                final String urlString = args.get(0).toString();
                final URL url = new URL(urlString);
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                String method = "GET";
                String bodyToSend = null;
                int timeout = 5000;

                if (args.size() > 1) {
                    final Value configVal = args.get(1);
                    Map<String, Value> options = null;

                    if (configVal.asJavaObject() instanceof String) {
                        final Value unpacked = SimpleJson.unpack(configVal.toString());
                        if (unpacked.asJavaObject() instanceof Map)
                            options = (Map<String, Value>) unpacked.asJavaObject();
                    } else if (configVal.asJavaObject() instanceof BotifyInstance) {
                        options = ((BotifyInstance) configVal.asJavaObject()).asMap();
                    } else if (configVal.asJavaObject() instanceof Map) {
                        options = (Map<String, Value>) configVal.asJavaObject();
                    }

                    if (options != null) {
                        if (options.containsKey("method"))
                            method = options.get("method").toString().toUpperCase();

                        if (options.containsKey("timeout"))
                            timeout = options.get("timeout").asInt();

                        if (options.containsKey("headers")) {
                            final Object headersObj = options.get("headers").asJavaObject();
                            if (headersObj instanceof Map) {
                                final Map<String, Value> headers = (Map<String, Value>) headersObj;
                                for (final Map.Entry<String, Value> entry : headers.entrySet())
                                    conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
                            }
                        }

                        if (options.containsKey("body")) {
                            final Value bodyVal = options.get("body");
                            if (bodyVal.asJavaObject() instanceof String) {
                                bodyToSend = bodyVal.toString();
                            } else {
                                bodyToSend = SimpleJson.pack(bodyVal.asJavaObject());
                                if (conn.getRequestProperty("Content-Type") == null)
                                    conn.setRequestProperty("Content-Type", "application/json");
                            }
                        }
                    }
                }

                conn.setRequestMethod(method);
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);

                if (conn.getRequestProperty("User-Agent") == null)
                    conn.setRequestProperty("User-Agent", "Botify/1.0");

                if (bodyToSend != null && !method.equals("GET")) {
                    conn.setDoOutput(true);
                    try (final OutputStream os = conn.getOutputStream()) {
                        final byte[] input = bodyToSend.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                }

                final BufferedReader in = getBufferedReader(conn);

                String inputLine;
                final StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    content.append(inputLine);

                in.close();

                return new Value(content.toString());
            } catch (final Exception e) {
                throw new RuntimeException("API Error: " + e.getMessage());
            }
        });
    }

    private static BufferedReader getBufferedReader(final HttpURLConnection conn) throws IOException {
        final int status = conn.getResponseCode();

        if (status > 299 && conn.getErrorStream() != null)
            return new BufferedReader(new InputStreamReader(conn.getErrorStream()));

        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }
}