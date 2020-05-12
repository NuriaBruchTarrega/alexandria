package nl.uva.alexandria.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    public FileManager() {
    }

    public String getClientLibraryJarPath(String pathToClientLibraryFolder, String clientLibraryJar) {
        File clientLibraryFolder = new File(pathToClientLibraryFolder);
        File[] filesInFolder = clientLibraryFolder.listFiles();
        assert filesInFolder != null : "Folder is empty";

        List<File> filteredFiles = Arrays.stream(filesInFolder).filter(file -> file.getName().contains(clientLibraryJar) && file.getName().endsWith(".jar")).collect(Collectors.toList());
        assert filteredFiles.size() != 0 : "No client Library jar found";
        assert filteredFiles.size() == 1 : "More than one jar match client library name";

        return filteredFiles.get(0).getAbsolutePath();
    }

    public List<String> getServerLibrariesJarPaths(String pathToClientLibraryFolder) {
        File dependencyFolder = new File(pathToClientLibraryFolder + "/target/dependency");
        File[] filesInFolder = dependencyFolder.listFiles();
        assert filesInFolder != null : "Folder is empty";

        List<File> filteredFiles = Arrays.stream(filesInFolder).filter(file -> file.getName().endsWith(".jar")).collect(Collectors.toList());
        assert filteredFiles.size() != 0 : "No jars found";

        List<String> jarPaths = filteredFiles.stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());

        return jarPaths;
    }
}
