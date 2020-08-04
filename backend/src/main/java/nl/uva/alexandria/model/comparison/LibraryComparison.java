package nl.uva.alexandria.model.comparison;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryComparison that = (LibraryComparison) o;
        return groupID.equals(that.groupID) &&
                artifactID.equals(that.artifactID) &&
                version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupID, artifactID, version);
    }
}
