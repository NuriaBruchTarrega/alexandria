package nl.uva.alexandria.model.comparison;

public class LibraryComparison {

    private String groupID;
    private String artifactID;
    private String version;
    private ComparisonData paperResults;
    private ComparisonData analysisResults;

    public LibraryComparison(String groupID, String artifactID, String version, ComparisonData paperResults, ComparisonData analysisResults) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
        this.paperResults = paperResults;
        this.analysisResults = analysisResults;
    }
}
