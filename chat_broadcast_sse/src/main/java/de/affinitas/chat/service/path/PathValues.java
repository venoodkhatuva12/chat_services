package de.affinitas.chat.service.path;

import java.util.Map;
import java.util.UUID;

public class PathValues {

    private final Map<String, String> keyValues;

    PathValues(Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public String string(String key) {
        throwIfNull(key);
        if ("".equals(key) || !keyValues.containsKey(key)) {
            throw new IllegalArgumentException("Unable to find key: \"" + key + "\" in path");
        }
        return keyValues.get(key);
    }

    private void throwIfNull(String input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }
    }

    public double doubleValue(String key) {
        return Double.parseDouble(string(key));
    }

    public UUID uuid(String key) {
        return UUID.fromString(string(key));
    }

    public String[] strings(String key, String delim) {
        return string(key).split(delim);
    }
}
