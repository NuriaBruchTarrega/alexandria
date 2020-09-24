package nl.uva.alexandria.model.experiments;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<Difference> compare() {
        if (analysisResults.hasMessage())
            return Optional.of(new Difference(libraryName(), this.analysisResults.getMessage()));

        Integer numDifferenceDirect = paperResults.getNumDirect() - analysisResults.getNumDirect();
        Integer numDifferenceTransitive = paperResults.getNumTransitive() - analysisResults.getNumTransitive();
        List<String> onlyAnalysisDirect = analysisResults.compareDirect(paperResults.getDependenciesDirect());
        List<String> onlyAnalysisTransitive = analysisResults.compareTransitive(paperResults.getDependenciesTransitive());
        List<String> onlyPaperDirect = paperResults.compareDirect(analysisResults.getDependenciesDirect());
        List<String> onlyPaperTransitive = paperResults.compareTransitive(analysisResults.getDependenciesTransitive());

        if (numDifferenceDirect == 0 && numDifferenceTransitive == 0 && onlyAnalysisDirect.isEmpty() && onlyAnalysisTransitive.isEmpty() && onlyPaperDirect.isEmpty() && onlyPaperTransitive.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Difference(libraryName(), numDifferenceDirect, numDifferenceTransitive, onlyAnalysisDirect, onlyPaperDirect, onlyAnalysisTransitive, onlyPaperTransitive));
    }

    private String libraryName() {
        return groupID + ':' + artifactID + ':' + version;
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
