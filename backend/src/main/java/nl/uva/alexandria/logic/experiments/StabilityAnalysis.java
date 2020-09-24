package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.experiments.StabilityAnalysisData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class StabilityAnalysis {

    private StabilityAnalysis() {
    }

    public static Set<StabilityAnalysisData> run(String pathToFile) {
        Set<StabilityAnalysisData> stabilityAnalysisDataSet = getStabilityAnalysisData(pathToFile);
        stabilityAnalysisDataSet.forEach(StabilityAnalysisData::runStabilityAnalysis);
        return stabilityAnalysisDataSet;
    }

    private static Set<StabilityAnalysisData> getStabilityAnalysisData(String pathToFile) {
        File file = new File(pathToFile);
        Set<StabilityAnalysisData> stabilityAnalysisDataSet = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String[] values = "\t".split(line);
                if (values.length == 3) {
                    Optional<DependencyTreeResult> dependencyTreeResultOptional = AnalysisRunner.analyzeLibrary(values[0], values[1], values[2]);
                    dependencyTreeResultOptional.ifPresent(dependencyTreeResult -> stabilityAnalysisDataSet.addAll(StabilityAnalysisData.from(dependencyTreeResult)));
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stabilityAnalysisDataSet;
    }
}
