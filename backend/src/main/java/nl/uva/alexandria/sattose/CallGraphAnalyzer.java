package nl.uva.alexandria.sattose;

import nl.uva.alexandria.model.sattose.SattoseFiles;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CallGraphAnalyzer {
    public void analyze(String pathToFolder) {
        SattoseFiles sattoseFiles = getSattoseFiles(pathToFolder);
        obtainMostUsedMethods(sattoseFiles);
    }

    private SattoseFiles getSattoseFiles(String pathToFolder) {
        SattoseFiles sattoseFiles = new SattoseFiles();
        File dataFolder = new File(pathToFolder);
        File[] listOfFiles = dataFolder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().contains("graphs")) sattoseFiles.callGraphsJson = file;
            else if (file.getName().contains("sattose_dependencies_final")) sattoseFiles.libraryDataTxt = file;
        }
        return sattoseFiles;
    }

    private void obtainMostUsedMethods(SattoseFiles sattoseFiles) {

    }
}
