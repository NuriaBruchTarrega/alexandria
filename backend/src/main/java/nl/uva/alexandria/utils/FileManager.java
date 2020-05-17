package nl.uva.alexandria.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class FileManager {

    public static String getClientLibraryJarPath(String pathToClientLibraryFolder, String clientLibraryJar) {
        File clientLibraryFolder = new File(pathToClientLibraryFolder);
        File[] filesInFolder = clientLibraryFolder.listFiles();
        assert filesInFolder != null : "Folder is empty";

        List<File> filteredFiles = Arrays.stream(filesInFolder).filter(file -> file.getName().contains(clientLibraryJar) && file.getName().endsWith(".jar")).collect(Collectors.toList());
        assert filteredFiles.size() != 0 : "No client Library jar found";
        assert filteredFiles.size() == 1 : "More than one jar match client library name";

        return filteredFiles.get(0).getAbsolutePath();
    }

    public static List<String> getServerLibrariesJarPaths(String pathToClientLibraryFolder) {
        File dependencyFolder = new File(pathToClientLibraryFolder + "/target/dependency");
        File[] filesInFolder = dependencyFolder.listFiles();
        assert filesInFolder != null : "Folder is empty";

        List<File> filteredFiles = Arrays.stream(filesInFolder).filter(file -> file.getName().endsWith(".jar")).collect(Collectors.toList());
        assert filteredFiles.size() != 0 : "No jars found";

        List<String> jarPaths = filteredFiles.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());

        return jarPaths;
    }

    public static List<String> getClassFiles(String clientLibraryJar) {
        File clientLibrary = new File(clientLibraryJar);
        assert clientLibrary != null : "Client Library not found";

        List<String> classes = new ArrayList<>();
        try {
            JarFile jar = new JarFile(clientLibrary);
            Enumeration<? extends JarEntry> enumeration = jar.entries();

            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = enumeration.nextElement();

                if (zipEntry.getName().endsWith(".class")) { // Get only class files
                    classes.add(zipEntry.getName()); // Relative path of class file in the jar.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }
}
