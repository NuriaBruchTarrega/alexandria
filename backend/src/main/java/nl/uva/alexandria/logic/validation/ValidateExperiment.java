package nl.uva.alexandria.logic.validation;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.validation.AnalysisSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(ValidateExperiment.class);

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
                    Optional<AnalysisSummary> analysisSummaryOpt = analyzeLibrary(values[0], values[1], values[2]);
                    analysisSummaryOpt.ifPresent(analysisSummarySet::add);
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return analysisSummarySet;
    }

    private static Optional<AnalysisSummary> analyzeLibrary(String groupId, String artifactId, String version) {
        Analyzer analyzer = new Analyzer();

        try {
            LOG.info("Starting analysis of: {}:{}:{}", groupId, artifactId, version);
            DependencyTreeResult result = analyzer.analyze(groupId, artifactId, version).getDependencyTreeResult();
            LOG.info("Finalized analysis of: {}:{}:{}", groupId, artifactId, version);
            return Optional.of(AnalysisSummary.from(result));
        } catch (RuntimeException e) {
            LOG.info("Analysis of: {}:{}:{} did not work, because {}", groupId, artifactId, version, e.getMessage());
        }

        return Optional.empty();
    }
}
