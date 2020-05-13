package nl.uva.alexandria.utils;

import java.util.Arrays;

public class ClassNameUtils {
    private static final String[] STANDARD_LIBRARIES = {"java.", "javax."};
    private static final String DEPENDENCY_LIBRARY = "/target/dependency/";
    private static final String JAR = ".jar";

    public static String getFullyQualifiedName(String classPath) {
        return classPath.replace(".class", "").replace("/", ".");
    }

    public static String arraySignatureToClassName(String signature) {
        while (signature.startsWith("[")) signature = signature.substring(1); // Remove all the nested array levels
        signature = signature.substring(1); // Remove the L in case it is an object, and the primitive type in case it is

        if (signature.length() == 0) return signature;

        signature = signature.substring(0, signature.length() - 1); // Signatures end with ';', which has to be removed to obtain the name of the class.

        return signature.replace("/", ".");
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
