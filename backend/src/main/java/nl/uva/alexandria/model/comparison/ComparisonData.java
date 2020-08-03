package nl.uva.alexandria.model.comparison;

public class ComparisonData {

    private Integer numDirect;
    private Integer numTransitive;
    private String[] dependenciesDirect;
    private String[] dependenciesTransitive;

    public ComparisonData(Integer numDirect, Integer numTransitive, String[] dependenciesDirect, String[] dependenciesTransitive) {
        this.numDirect = numDirect;
        this.numTransitive = numTransitive;
        this.dependenciesDirect = dependenciesDirect;
        this.dependenciesTransitive = dependenciesTransitive;
    }
}
