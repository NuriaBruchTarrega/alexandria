package nl.uva.alexandria.logic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class FileManager {

    private static final Logger LOG = LoggerFactory.getLogger(FileManager.class);

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
            LOG.error("Error retrieving class files\n\n{}", stackTraceToString(e));
        }

        return classes;
    }
}
