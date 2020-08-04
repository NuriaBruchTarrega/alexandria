package nl.uva.alexandria.model.comparison;

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
}
