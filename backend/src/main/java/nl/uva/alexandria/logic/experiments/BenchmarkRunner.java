package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class BenchmarkRunner {
    public static void run(String pathToFile) {
        runAllAnalysis(pathToFile);
    }

    private static void runAllAnalysis(String pathToFile) {
        File file = new File(pathToFile);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String[] values = line.split("\t");
                if (values.length == 3) {
                    Optional<DependencyTreeResult> dependencyTreeResultOptional = AnalysisRunner.analyzeLibrary(values[0], values[1], values[2]);
                    // TODO: do something with the result
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }
}
