package nl.uva.alexandria.model.comparison;

import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.*;

public class LibraryComparisonFactory {
    private LibraryComparisonFactory() {
    }

    public static LibraryComparison createLibraryComparisonFromValues(String[] values) {
        // Values: [0] GroupID [1] ArtifactID [2] version [3] Direct [4] Transitive [5] DependenciesD [6] DependenciesT
        LibraryComparison libraryComparison = new LibraryComparison(values[0], values[1], values[2]);

        List<String> dependenciesDirect = createListDependencies(values[5]);
        List<String> dependenciesTransitive = createListDependencies(values[6]);
        ComparisonData comparisonData = new ComparisonData(Integer.valueOf(values[3]), Integer.valueOf(values[4]), dependenciesDirect, dependenciesTransitive);
        libraryComparison.setPaperResults(comparisonData);

        return libraryComparison;
    }

    public static ComparisonData createComparisonDataFromDependencyTreeResult(DependencyTreeResult dependencyTreeResult) {
        Integer numDirect = 0;
        Integer numTransitive = 0;
        List<String> dependenciesDirect = new ArrayList<>();
        List<String> dependenciesTransitive = new ArrayList<>();
        List<DependencyTreeResult> directDependencies = dependencyTreeResult.getChildren();

        Queue<DependencyTreeResult> toVisit = new ArrayDeque<>(directDependencies);

        while (!toVisit.isEmpty()) {
            DependencyTreeResult visiting = toVisit.poll();

            if (visiting.isUnused()) {
                if (directDependencies.contains(visiting)) {
                    numDirect += 1;
                    dependenciesDirect.add(visiting.getLibrary().toString());
                } else {
                    numTransitive += 1;
                    dependenciesTransitive.add(visiting.getLibrary().toString());
                }
            }

            toVisit.addAll(visiting.getChildren());
        }

        return new ComparisonData(numDirect, numTransitive, dependenciesDirect, dependenciesTransitive);
    }

    private static List<String> createListDependencies(String values) {
        String[] split = values.split(",");
        if (split.length == 1 && split[0].length() == 1) return Collections.emptyList();
        return Arrays.asList(split);
    }
}
