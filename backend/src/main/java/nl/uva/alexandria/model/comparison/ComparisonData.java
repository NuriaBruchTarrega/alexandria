package nl.uva.alexandria.model.comparison;

import java.util.ArrayList;
import java.util.List;

public class ComparisonData {

    private Integer numDirect;
    private Integer numTransitive;
    private List<String> dependenciesDirect;
    private List<String> dependenciesTransitive;

    public ComparisonData(Integer numDirect, Integer numTransitive, List<String> dependenciesDirect, List<String> dependenciesTransitive) {
        this.numDirect = numDirect;
        this.numTransitive = numTransitive;
        this.dependenciesDirect = dependenciesDirect;
        this.dependenciesTransitive = dependenciesTransitive;
    }

    public Integer getNumDirect() {
        return numDirect;
    }

    public Integer getNumTransitive() {
        return numTransitive;
    }

    public List<String> getDependenciesDirect() {
        return dependenciesDirect;
    }

    public List<String> getDependenciesTransitive() {
        return dependenciesTransitive;
    }

    public List<String> compareDirect(List<String> toCompare) {
        return compareDependencies(this.dependenciesDirect, toCompare);
    }

    public List<String> compareTransitive(List<String> toCompare) {
        return compareDependencies(this.dependenciesTransitive, toCompare);
    }

    private List<String> compareDependencies(List<String> current, List<String> toCompare) {
        List<String> comparison = new ArrayList<>();
        current.forEach(dependency -> {
            boolean included = toCompare.stream().anyMatch(dependencyToCompare -> dependency.contains(dependencyToCompare) || dependencyToCompare.contains(dependency));
            if (!included) comparison.add(dependency);
        });

        return comparison;
    }
}
