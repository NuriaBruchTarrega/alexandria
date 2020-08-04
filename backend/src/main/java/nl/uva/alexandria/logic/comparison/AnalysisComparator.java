package nl.uva.alexandria.logic.comparison;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.logic.exceptions.FileException;
import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.comparison.ComparisonData;
import nl.uva.alexandria.model.comparison.Difference;
import nl.uva.alexandria.model.comparison.LibraryComparison;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static nl.uva.alexandria.model.comparison.LibraryComparisonFactory.createComparisonDataFromDependencyTreeResult;
import static nl.uva.alexandria.model.comparison.LibraryComparisonFactory.createLibraryComparisonFromValues;

@Component
public class AnalysisComparator {

    public void compare(String pathToFile) {
        // Open file.
        File file = new File(pathToFile);

        Set<LibraryComparison> libraryComparisonSet;
        try {
            libraryComparisonSet = createLibraryComparisonSet(file);
        } catch (IOException e) {
            throw new FileException(e.getMessage());
        }

        doRequests(libraryComparisonSet);
        var comparison = compareResults(libraryComparisonSet);
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
            DependencyTreeResult result = analyzer.analyze(libraryComparison.getGroupID(), libraryComparison.getArtifactID(), libraryComparison.getVersion()).getDependencyTreeResult();
            ComparisonData comparisonData = createComparisonDataFromDependencyTreeResult(result);
            libraryComparison.setAnalysisResults(comparisonData);
        });
    }

    private Set<Difference> compareResults(Set<LibraryComparison> libraryComparisonSet) {
        Set<Difference> differences = new HashSet<>();
        libraryComparisonSet.forEach(libraryComparison -> libraryComparison.compare().ifPresent(differences::add));
        return differences;
    }
}
