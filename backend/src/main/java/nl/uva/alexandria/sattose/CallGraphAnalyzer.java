package nl.uva.alexandria.sattose;

import nl.uva.alexandria.model.sattose.ExternalCall;
import nl.uva.alexandria.model.sattose.LibraryCallGraph;
import nl.uva.alexandria.model.sattose.LibraryCallGraphFactory;
import nl.uva.alexandria.model.sattose.SattoseFiles;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CallGraphAnalyzer {
    public List<Map.Entry<String, Integer>> analyze(String pathToFolder) {
        Map<String, Integer> mostUsedMethods = new HashMap<>();
        SattoseFiles sattoseFiles = getSattoseFiles(pathToFolder);

        try {
            mostUsedMethods = calculateMostUsedMethods(sattoseFiles);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return mostUsedMethods.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
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

    private Map<String, Integer> calculateMostUsedMethods(SattoseFiles sattoseFiles) throws IOException, ParseException {
        Map<String, Integer> mostUsedMethods = new HashMap<>();
        JSONParser parser = new JSONParser();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(sattoseFiles.callGraphsJson));
        String line = bufferedReader.readLine();

        while (line != null) {
            JSONObject callGraphJSON = (JSONObject) parser.parse(line);
            Map<String, Integer> externalMethodsCalled = getExternalMethodsCalled(LibraryCallGraphFactory.getLibraryCallGraphFromJson(callGraphJSON));
            externalMethodsCalled.forEach((method, num) -> {
                mostUsedMethods.computeIfPresent(method, (key, value) -> value + num);
                mostUsedMethods.putIfAbsent(method, num);
            });
            line = bufferedReader.readLine();
        }

        return mostUsedMethods;
    }


    private Map<String, Integer> getExternalMethodsCalled(LibraryCallGraph libraryCallGraph) {
        Map<String, Integer> externalMethodsCalled = new HashMap<>();

        List<ExternalCall> externalCalls = libraryCallGraph.getGraph().getExternallCalls();
        externalCalls.forEach(externalCall -> {
            String targetMethod = externalCall.getTargetMethod();
            if (!targetMethod.startsWith("///java.lang") && !targetMethod.startsWith("///java.util")) {
                externalMethodsCalled.computeIfPresent(targetMethod, (key, value) -> value + 1);
                externalMethodsCalled.putIfAbsent(targetMethod, 1);
            }
        });

        return externalMethodsCalled;
    }
}
