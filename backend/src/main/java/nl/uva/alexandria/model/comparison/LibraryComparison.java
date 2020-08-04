package nl.uva.alexandria.model.comparison;

public class LibraryComparison {

    private String groupID;
    private String artifactID;
    private String version;
    private ComparisonData paperResults;
    private ComparisonData analysisResults;

    public LibraryComparison(String groupID, String artifactID, String version) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    public ComparisonData getPaperResults() {
        return paperResults;
    }

    public ComparisonData getAnalysisResults() {
        return analysisResults;
    }

    public void setPaperResults(ComparisonData paperResults) {
        this.paperResults = paperResults;
    }

    public void setAnalysisResults(ComparisonData analysisResults) {
        this.analysisResults = analysisResults;
    }
}
