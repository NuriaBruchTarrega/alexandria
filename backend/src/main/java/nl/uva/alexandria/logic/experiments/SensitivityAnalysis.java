package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.experiments.SensitivityAnalysisData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class SensitivityAnalysis {

    private SensitivityAnalysis() {
    }

    public static Set<SensitivityAnalysisData> run(String pathToFile) {
        Set<SensitivityAnalysisData> sensitivityAnalysisDataSet = getSensitivityAnalysisData(pathToFile);
        sensitivityAnalysisDataSet.forEach(SensitivityAnalysisData::runSensitivityAnalysis);
        return sensitivityAnalysisDataSet;
    }

    private static Set<SensitivityAnalysisData> getSensitivityAnalysisData(String pathToFile) {
        File file = new File(pathToFile);
        Set<SensitivityAnalysisData> sensitivityAnalysisDataSet = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String[] values = line.split("\t");
                if (values.length == 3) {
                    Optional<DependencyTreeResult> dependencyTreeResultOptional = AnalysisRunner.analyzeLibrary(values[0], values[1], values[2]);
                    dependencyTreeResultOptional.ifPresent(dependencyTreeResult -> sensitivityAnalysisDataSet.addAll(SensitivityAnalysisData.from(dependencyTreeResult)));
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sensitivityAnalysisDataSet;
    }
}
