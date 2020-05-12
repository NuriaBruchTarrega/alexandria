package nl.uva.alexandria.utils;

import java.util.Arrays;

public class ClassNameUtils {
    private static final String[] STANDARD_LIBRARIES = {"java.", "javax."};

    public static String getFullyQualifiedName(String classPath) {
        return classPath.replace(".class", "").replace("/", ".");
    }

    public static boolean isClientOrStandardLibrary(String className, String client) {
        if (className.startsWith(client)) return true;
        else return Arrays.stream(STANDARD_LIBRARIES).anyMatch(l -> className.startsWith(l));
    }
}
