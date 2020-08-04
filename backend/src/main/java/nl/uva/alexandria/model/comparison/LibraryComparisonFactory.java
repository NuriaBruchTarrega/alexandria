package nl.uva.alexandria.model.comparison;

import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.Arrays;
import java.util.List;

public class LibraryComparisonFactory {
    private LibraryComparisonFactory() {
    }

    public static LibraryComparison createLibraryComparisonFromValues(String[] values) {
        // Values: [0] GroupID [1] ArtifactID [2] version [3] Direct [4] Transitive [5] DependenciesD [6] DependenciesT
        LibraryComparison libraryComparison = new LibraryComparison(values[0], values[1], values[2]);

        List<String> dependenciesDirect = Arrays.asList(values[5].split(","));
        List<String> dependenciesTransitive = Arrays.asList(values[6].split(","));
        ComparisonData comparisonData = new ComparisonData(Integer.valueOf(values[3]), Integer.valueOf(values[4]), dependenciesDirect, dependenciesTransitive);
        libraryComparison.setPaperResults(comparisonData);

        return libraryComparison;
    }
}
