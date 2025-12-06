package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class FetchNativeFunction extends NativeFunction {

    public FetchNativeFunction() {
        super((env, args) -> {
            try {
                if (args.isEmpty())
                    throw new RuntimeException("Function 'fetch' requires at least 1 argument.");

                final String urlString = args.get(0).toString();
                final URL url = new URL(urlString);
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                String method = "GET";
                if (args.size() > 1) {
                    final String inputMethod = args.get(1).toString().toUpperCase();
                    switch (inputMethod) {
                        case "GET":
                        case "POST":
                        case "PUT":
                        case "PATCH":
                        case "DELETE":
                            method = inputMethod;
                            break;
                    }
                }

                conn.setRequestMethod(method);
                conn.setRequestProperty("User-Agent",
                        args.size() > 2
                                ? args.get(2).toString()
                                : "Botify/1.0"
                );

                final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    content.append(inputLine);

                in.close();
                return new Value(content.toString());
            } catch (final Exception e) {
                throw new RuntimeException("API Error: " + e.getMessage());
            }
        });
    }

}
