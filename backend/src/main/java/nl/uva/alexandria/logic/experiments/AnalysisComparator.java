package nl.uva.alexandria.logic.experiments;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.logic.exceptions.FileException;
import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.experiments.ComparisonData;
import nl.uva.alexandria.model.experiments.Difference;
import nl.uva.alexandria.model.experiments.LibraryComparison;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static nl.uva.alexandria.model.experiments.LibraryComparisonFactory.createComparisonDataFromDependencyTreeResult;
import static nl.uva.alexandria.model.experiments.LibraryComparisonFactory.createLibraryComparisonFromValues;

@Component
public class AnalysisComparator {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisComparator.class);


    public Set<Difference> compare(String pathToFile) {
        File file = new File(pathToFile);

        Set<LibraryComparison> libraryComparisonSet;
        try {
            libraryComparisonSet = createLibraryComparisonSet(file);
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }

        doRequests(libraryComparisonSet);
        return compareResults(libraryComparisonSet);
    }

    private Set<LibraryComparison> createLibraryComparisonSet(File file) throws IOException {
        Set<LibraryComparison> libraryComparisonSet = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line = bufferedReader.readLine(); // Ignore the headers
            line = bufferedReader.readLine();

            while (line != null) {
                // Read the line
                String[] values = line.split("\t");
                LibraryComparison newLibraryComparison = createLibraryComparisonFromValues(values);
                libraryComparisonSet.add(newLibraryComparison);
                line = bufferedReader.readLine();
            }
        }

        return libraryComparisonSet;
    }

    private void doRequests(Set<LibraryComparison> libraryComparisonSet) {
        Analyzer analyzer = new Analyzer();

        libraryComparisonSet.forEach(libraryComparison -> {
            try {
                DependencyTreeResult result = analyzer.analyze(libraryComparison.getGroupID(), libraryComparison.getArtifactID(), libraryComparison.getVersion()).getDependencyTreeResult();
                ComparisonData comparisonData = createComparisonDataFromDependencyTreeResult(result);
                libraryComparison.setAnalysisResults(comparisonData);
                LOG.info("Finalized analysis of: {}:{}:{}", libraryComparison.getGroupID(), libraryComparison.getArtifactID(), libraryComparison.getVersion());
            } catch (RuntimeException e) {
                libraryComparison.setAnalysisResults(new ComparisonData(e.getMessage()));
                LOG.info("Analysis of: {}:{}:{} did not work, because {}", libraryComparison.getGroupID(), libraryComparison.getArtifactID(), libraryComparison.getVersion(), e.getMessage());
            }
        });
    }

    private Set<Difference> compareResults(Set<LibraryComparison> libraryComparisonSet) {
        Set<Difference> differences = new HashSet<>();
        libraryComparisonSet.forEach(libraryComparison -> libraryComparison.compare().ifPresent(differences::add));
        return differences;
    }
}
