package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.DependencyTreeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AnalysisRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisRunner.class);

    private AnalysisRunner() {
    }

    public static Optional<DependencyTreeResult> analyzeLibrary(String groupId, String artifactId, String version) {
        Analyzer analyzer = new Analyzer();

        try {
            LOG.info("Starting analysis of: {}:{}:{}", groupId, artifactId, version);
            DependencyTreeResult result = analyzer.analyze(groupId, artifactId, version).getDependencyTreeResult();
            LOG.info("Finalized analysis of: {}:{}:{}", groupId, artifactId, version);
            return Optional.of(result);
        } catch (RuntimeException e) {
            LOG.info("Analysis of: {}:{}:{} did not work, because {}", groupId, artifactId, version, e.getMessage());
        }

        return Optional.empty();
    }
}
