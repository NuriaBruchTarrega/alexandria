package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.experiments.AnalysisSummary;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class ValidateExperiment {

    private ValidateExperiment() {
    }

    public static Set<AnalysisSummary> run(String pathToFile) {
        File file = new File(pathToFile);
        return runAnalysis(file);
    }

    private static Set<AnalysisSummary> runAnalysis(File file) {
        Set<AnalysisSummary> analysisSummarySet = new HashSet<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                // Read the line
                String[] values = line.split("\t");
                if (values.length == 3) {
                    Optional<DependencyTreeResult> dependencyTreeResultOptional = AnalysisRunner.analyzeLibrary(values[0], values[1], values[2]);
                    dependencyTreeResultOptional.ifPresent(dependencyTreeResult -> analysisSummarySet.add(AnalysisSummary.from(dependencyTreeResult)));
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return analysisSummarySet;
    }
}
