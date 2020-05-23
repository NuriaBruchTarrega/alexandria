package nl.uva.alexandria.logic.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassNameUtils {

    private static final String JAR = ".jar";
    private static final String REPOSITORY = "/.m2/repository/";

    public static String getFullyQualifiedNameFromClassPath(String classPath) {
        classPath = classPath.replace(File.separator, "/");
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

    public static String getJarPathFromClassPath(String classPath) {
        int indexJar = classPath.lastIndexOf(JAR);
        return classPath.substring(0, indexJar + JAR.length());
    }

    public static String[] getGroupArtifactVersionFromJarPath(String jarPath) {
        jarPath = jarPath.replace(File.separator, "/");
        int indexRepository = jarPath.lastIndexOf(REPOSITORY);
        String substr = jarPath.substring(indexRepository + REPOSITORY.length(), jarPath.length() - JAR.length());

        int indexSeparator = substr.lastIndexOf("/");
        substr = substr.substring(0, indexSeparator);

        indexSeparator = substr.lastIndexOf("/");
        String version = substr.substring(indexSeparator + 1);
        substr = substr.substring(0, indexSeparator);

        indexSeparator = substr.lastIndexOf("/");
        String artifact = substr.substring(indexSeparator + 1);
        String group = substr.substring(0, indexSeparator);
        group = group.replace("/", ".");


        return new String[]{group, artifact, version};
    }
}
