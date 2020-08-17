package nl.uva.alexandria.logic.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassNameUtils {

    private static final String JAR = ".jar";
    private static final String CLASS = ".class";
    private static final String EMPTY = "";
    private static final String DOT = ".";
    private static final String SEMICOLON = ";";
    private static final String SLASH = "/";
    private static final String ARRAY = "[";
    private static final String SIGNATURE_REGEX = "[-<;>+*]+";

    private ClassNameUtils() {
    }

    public static String getFullyQualifiedNameFromClassPath(String classPath) {
        classPath = classPath.replace(File.separator, SLASH);
        return classPath.replace(CLASS, EMPTY).replace(SLASH, DOT);
    }

    public static String signatureToClassName(String signature) {
        while (signature.startsWith(ARRAY)) signature = signature.substring(1); // Remove all the nested array levels
        signature = signature.substring(1); // Remove the L in case it is an object, and the primitive type in case it is

        if (signature.length() == 0) return signature;

        // Signatures end with ';', which has to be removed to obtain the name of the class.
        // If the signature comes from a generic signature, it may not end in ';'
        if (signature.endsWith(SEMICOLON)) signature = signature.substring(0, signature.length() - 1);

        return signature.replace(SLASH, DOT);
    }

    public static List<String> getClassNamesFromGenericSignature(String signature) {
        List<String> classNames = new ArrayList<>();

        List<String> split = Arrays.asList(signature.split(SIGNATURE_REGEX));
        List<String> types = split.stream().filter(s -> s.contains(SLASH)).collect(Collectors.toList()); // Discard non-specified types and primitives

        types.forEach(t -> {
            String className = signatureToClassName(t); // Here, arrays and primitives are discarded
            if (className.length() != 0) classNames.add(className);
        });

        return classNames;
    }

    public static String getJarPathFromClassPath(String classPath) {
        int indexJar = classPath.lastIndexOf(JAR);
        return classPath.substring(0, indexJar + JAR.length());
    }
}
