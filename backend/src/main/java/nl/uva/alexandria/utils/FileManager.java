package nl.uva.alexandria.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager {
    public FileManager() {
    }

    public static String getClientLibraryPath(String pathToClientLibraryFolder, String clientLibraryJar) throws FileNotFoundException {
        File clientLibraryFolder = new File(pathToClientLibraryFolder);
        File[] filesInFolder = clientLibraryFolder.listFiles();
        List<File> filteredFiles = Arrays.stream(filesInFolder).filter(file -> file.getName().contains(clientLibraryJar) && file.getName().contains(".jar")).collect(Collectors.toList());
        if (filteredFiles.size() != 1) throw new FileNotFoundException();
        return filteredFiles.get(0).getAbsolutePath();
    }
}
