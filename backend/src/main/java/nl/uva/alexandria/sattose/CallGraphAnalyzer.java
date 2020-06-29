package nl.uva.alexandria.sattose;

import nl.uva.alexandria.model.sattose.Factory;
import nl.uva.alexandria.model.sattose.LibraryCallGraph;
import nl.uva.alexandria.model.sattose.SattoseFiles;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class CallGraphAnalyzer {
    public void analyze(String pathToFolder) {
        SattoseFiles sattoseFiles = getSattoseFiles(pathToFolder);
        try {
            Set<LibraryCallGraph> callGraphs = getLibraryCallGraphsFromFile(sattoseFiles);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    private Set<LibraryCallGraph> getLibraryCallGraphsFromFile(SattoseFiles sattoseFiles) throws IOException, ParseException {
        Set<LibraryCallGraph> callGraphs = new HashSet<>();
        JSONParser parser = new JSONParser();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(sattoseFiles.callGraphsJson));
        String line = bufferedReader.readLine();

        while (line != null) {
            JSONObject callGraphJSON = (JSONObject) parser.parse(line);
            callGraphs.add(Factory.getLibraryCallGraphFromJson(callGraphJSON));
            line = bufferedReader.readLine();
        }

        return callGraphs;
    }

    private void obtainMostUsedMethods(SattoseFiles sattoseFiles) {

    }
}
