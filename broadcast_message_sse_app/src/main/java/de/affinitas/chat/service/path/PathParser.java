package de.affinitas.chat.service.path;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class PathParser {

    private final List<String> templatedKeys;
    private String template;

    public PathParser(String template) {
        throwIfNull(template);
        templatedKeys = parseTemplateForKeys(template);
        this.template = template;
    }

    public PathValues parse(String uriString) {
        throwIfNull(uriString);
        return new PathValues(parsePathForValues(uriString));
    }

    private List<String> parseTemplateForKeys(String template) {
        String[] splitTemplate = template.split("/");
        LinkedList<String> keys = new LinkedList<>();

        for (String currentPathPart : splitTemplate) {
            if (currentPathPart.startsWith("{") && currentPathPart.endsWith("}")) {
                String key = currentPathPart.substring(1, currentPathPart.length() - 1).trim();
                checkIfKeyValid(key, keys);
                keys.add(key);
            } else {
                keys.add("");
            }
        }
        return keys;
    }

    private void checkIfKeyValid(String key, List<String> otherKeys) {
        key = key.trim();

        if ("".equals(key)) {
            throw new IllegalArgumentException("Key in template: " + template + " has a key defined as blank");
        }
        if (otherKeys.contains(key)) {
            throw new IllegalArgumentException("More than one key: " + key + " has been defined for the template: " + template);
        }
    }

    private Map<String, String> parsePathForValues(String path) {
        Map<String, String> reply = new LinkedHashMap<>();
        String[] splitPath = path.split("/");
        for (int i = 0; i < splitPath.length; i++) {
            String currentKey;
            try {
                currentKey = templatedKeys.get(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            if (currentKey != null) {
                String currentValue = splitPath[i];
                reply.put(currentKey, currentValue);
            }
        }
        return reply;
    }

    public static void throwIfNull(Object input) {
        if (input == null) { throw new IllegalArgumentException(); }
    }

}
