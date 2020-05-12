package nl.uva.alexandria.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    public FileManager() {
    }

    public String getClientLibraryPath(String pathToClientLibraryFolder, String clientLibraryJar) {
        File clientLibraryFolder = new File(pathToClientLibraryFolder);
        File[] filesInFolder = clientLibraryFolder.listFiles();
        assert filesInFolder != null : "Folder is empty";

        List<File> filteredFiles = Arrays.stream(filesInFolder).filter(file -> file.getName().contains(clientLibraryJar) && file.getName().contains(".jar")).collect(Collectors.toList());
        assert filteredFiles.size() != 0 : "No client Library jar found";
        assert filteredFiles.size() == 1 : "More than one jar match client library name";

        return filteredFiles.get(0).getAbsolutePath();
    }
}
