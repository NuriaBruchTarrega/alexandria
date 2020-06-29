package nl.uva.alexandria.sattose;

import nl.uva.alexandria.model.sattose.SattoseFiles;
import org.springframework.stereotype.Component;

@Component
public class CallGraphAnalyzer {
    public void analyze(String pathToFolder) {
        SattoseFiles sattoseFiles = getSattoseFiles(pathToFolder);
        obtainMostUsedMethods(sattoseFiles);
    }

    private SattoseFiles getSattoseFiles(String pathToFolder) {

        return null;
    }

    private void obtainMostUsedMethods(SattoseFiles sattoseFiles) {

    }
}
