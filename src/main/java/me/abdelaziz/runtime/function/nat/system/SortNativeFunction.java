package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

import java.util.*;

public final class SortNativeFunction extends NativeFunction {

    public SortNativeFunction() {
        super((env, args) -> {
            if (args.isEmpty()) {
                throw new RuntimeException("Function 'sort' requires 1 argument.");
            }

            final Value target = args.get(0);
            final Object obj = target.asJavaObject();

            if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                final List<Value> list = (List<Value>) obj;

                list.sort(SortNativeFunction::compare);

                return target;
            }

            if (obj instanceof String) {
                final char[] chars = ((String) obj).toCharArray();

                Arrays.sort(chars);
                return new Value(new String(chars));
            }

            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                final Map<String, Value> map = (Map<String, Value>) obj;

                final Map<String, Value> sorted = new LinkedHashMap<>();

                map.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

                return new Value(sorted);
            }

            return target;
        });
    }

    private static int compare(final Value v1, final Value v2) {
        final Object o1 = v1.asJavaObject(),
                o2 = v2.asJavaObject();

        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        if (o1 instanceof Number && o2 instanceof Number)
            return Double.compare(((Number) o1).doubleValue(), ((Number) o2).doubleValue());

        return o1.toString().compareTo(o2.toString());
    }
}