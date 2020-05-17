package nl.uva.alexandria.logic.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassNameUtils {

    private static final String DEPENDENCY_LIBRARY = "/target/dependency/";
    private static final String JAR = ".jar";

    public static String getFullyQualifiedNameFromClassPath(String classPath) {
        return classPath.replace(".class", "").replace("/", ".");
    }

    public static String signatureToClassName(String signature) {
        while (signature.startsWith("[")) signature = signature.substring(1); // Remove all the nested array levels
        signature = signature.substring(1); // Remove the L in case it is an object, and the primitive type in case it is

        if (signature.length() == 0) return signature;

        // Signatures end with ';', which has to be removed to obtain the name of the class.
        // If the signature comes from a generic signature, it may not end in ';'
        if (signature.endsWith(";")) signature = signature.substring(0, signature.length() - 1);

        return signature.replace("/", ".");
    }

    public static String getLibraryName(String path) {
        path = path.replace("\\", "/"); // In case it is run in windows
        int indexDep = path.lastIndexOf(DEPENDENCY_LIBRARY);
        String substringDep = path.substring(indexDep + DEPENDENCY_LIBRARY.length());
        int indexJar = substringDep.indexOf(JAR);
        return substringDep.substring(0, indexJar);
    }

    public static List<String> getClassNamesFromGenericSignature(String signature) {
        List<String> classNames = new ArrayList<>();

        List<String> split = Arrays.asList(signature.split("[-<;>+*]+"));
        List<String> types = split.stream().filter(s -> s.contains("/")).collect(Collectors.toList()); // Discard non-specified types and primitives

        types.forEach(t -> {
            String className = signatureToClassName(t); // Here, arrays and primitives are discarded
            if (className.length() != 0) classNames.add(className);
        });

        return classNames;
    }
}
