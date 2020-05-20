package nl.uva.alexandria.logic.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FileManager {

    public static List<String> getClassFiles(String clientLibraryJar) {
        File clientLibrary = new File(clientLibraryJar);

        List<String> classes = new ArrayList<>();
        try {
            JarFile jar = new JarFile(clientLibrary);
            Enumeration<? extends JarEntry> enumeration = jar.entries();

            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = enumeration.nextElement();

                if (zipEntry.getName().endsWith(".class") && !zipEntry.getName().contains("META-INF")) { // Get only class files
                    classes.add(zipEntry.getName()); // Relative path of class file in the jar.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }
}
