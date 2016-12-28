package de.affinitas.chat.service.path;

import java.net.URL;

public final class PathResolver {

    private PathResolver(){}

    public static String resolve(String path) {
        final URL resource = PathResolver.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("File root: " + path + " not found on classpath");
        }
        return resource.toExternalForm();

    }

    public static String absolutePath(String path) {
        final URL resource = PathResolver.class.getClassLoader().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("File root: " + path + " not found on classpath");
        }
        return resource.getPath();

    }

}
