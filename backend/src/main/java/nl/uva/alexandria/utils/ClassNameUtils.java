package nl.uva.alexandria.utils;

import java.util.Arrays;

public class ClassNameUtils {
    private static final String[] STANDARD_LIBRARIES = {"java.", "javax."};
    private static final String DEPENDENCY_LIBRARY = "/target/dependency/";
    private static final String JAR = ".jar";

    public static String getFullyQualifiedName(String classPath) {
        return classPath.replace(".class", "").replace("/", ".");
    }

    public static boolean isClientOrStandardLibrary(String className, String client) {
        if (className.startsWith(client)) return true;
        else return Arrays.stream(STANDARD_LIBRARIES).anyMatch(l -> className.startsWith(l));
    }

    public static String getLibraryName(String path) {
        int indexDep = path.lastIndexOf(DEPENDENCY_LIBRARY);
        String substringDep = path.substring(indexDep + DEPENDENCY_LIBRARY.length());
        int indexJar = substringDep.indexOf(JAR);
        return substringDep.substring(0, indexJar);
    }
}
