package me.abdelaziz.util;

import me.abdelaziz.runtime.BotifyInstance;
import me.abdelaziz.runtime.Value;
import java.util.*;

public final class SimpleJson {

    public static String pack(final Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Value) return pack(((Value) obj).asJavaObject());
        if (obj instanceof BotifyInstance) return pack(((BotifyInstance) obj).asMap());
        if (obj instanceof String) return "\"" + obj + "\"";

        if (obj instanceof Map) {
            final StringBuilder sb = new StringBuilder("{");
            final Map<?, ?> map = (Map<?, ?>) obj;
            int i = 0;

            for (final Map.Entry<?, ?> entry : map.entrySet()) {
                if (i++ > 0) sb.append(", ");
                sb.append(pack(entry.getKey())).append(": ").append(pack(entry.getValue()));
            }

            return sb.append("}").toString();
        }

        if (obj instanceof List) {
            final StringBuilder sb = new StringBuilder("[");
            final List<?> list = (List<?>) obj;

            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(pack(list.get(i)));
            }

            return sb.append("]").toString();
        }

        return obj.toString();
    }

    private static int pos;
    private static String json;

    public static Value unpack(final String jsonString) {
        json = jsonString.trim();
        pos = 0;

        return new Value(parseValue());
    }

    private static Object parseValue() {
        skipWhitespace();

        final char c = peek();

        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();

        if (Character.isDigit(c) || c == '-') return parseNumber();

        if (json.startsWith("true", pos)) {
            pos += 4;
            return true;
        }

        if (json.startsWith("false", pos)) {
            pos += 5;
            return false;
        }

        if (json.startsWith("null", pos)) {
            pos += 4;
            return null;
        }

        return null;
    }

    private static Map<String, Value> parseObject() {
        final Map<String, Value> map = new HashMap<>();
        pos++;
        while (peek() != '}') {
            skipWhitespace();

            final String key = (String) parseValue();

            skipWhitespace();
            pos++;

            final Value value = new Value(parseValue());
            map.put(key, value);
            skipWhitespace();
            if (peek() == ',')
                pos++;
        }

        pos++;
        return map;
    }

    private static List<Value> parseArray() {
        final List<Value> list = new ArrayList<>();
        pos++;

        while (peek() != ']') {
            list.add(new Value(parseValue()));
            skipWhitespace();
            if (peek() == ',') pos++;
        }

        pos++;
        return list;
    }

    private static String parseString() {
        pos++;

        final StringBuilder sb = new StringBuilder();
        while (peek() != '"')
            sb.append(json.charAt(pos++));

        pos++;

        return sb.toString();
    }

    private static Double parseNumber() {
        final int start = pos;
        if (peek() == '-') pos++;

        while (Character.isDigit(peek()) || peek() == '.')
            pos++;

        return Double.parseDouble(json.substring(start, pos));
    }

    private static void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos)))
            pos++;
    }

    private static char peek() {
        return pos >= json.length() ? 0 : json.charAt(pos);
    }
}