package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.experiments.BenchmarkResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class BenchmarkRunner {

    private BenchmarkRunner() {
    }

    public static BenchmarkResult run(String pathToFile) {
        File file = new File(pathToFile);
        BenchmarkResult benchmarkResult = new BenchmarkResult();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine();

            while (line != null) {
                String[] values = line.split("\t");
                if (values.length == 3) {
                    Optional<DependencyTreeResult> dependencyTreeResultOptional = AnalysisRunner.analyzeLibrary(values[0], values[1], values[2]);
                    dependencyTreeResultOptional.ifPresent(benchmarkResult::addDependencyTreeResult);
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return benchmarkResult;
    }
}
