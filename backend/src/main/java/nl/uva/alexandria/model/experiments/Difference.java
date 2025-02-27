package nl.uva.alexandria.model.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Difference {
    private String library;
    private String message;
    private Integer numDirectDifference;
    private Integer numTransitiveDifference;
    private List<String> onlyByAnalysisDirect;
    private List<String> onlyByPaperDirect;
    private List<String> onlyByAnalysisTransitive;
    private List<String> onlyByPaperTransitive;

    public Difference(String library, Integer numDirectDifference, Integer numTransitiveDifference, List<String> onlyByAnalysisDirect, List<String> onlyByPaperDirect, List<String> onlyByAnalysisTransitive, List<String> onlyByPaperTransitive) {
        this.library = library;
        this.message = "";
        this.numDirectDifference = numDirectDifference;
        this.numTransitiveDifference = numTransitiveDifference;
        this.onlyByAnalysisDirect = onlyByAnalysisDirect;
        this.onlyByPaperDirect = onlyByPaperDirect;
        this.onlyByAnalysisTransitive = onlyByAnalysisTransitive;
        this.onlyByPaperTransitive = onlyByPaperTransitive;
    }

    public Difference(String library, String message) {
        this.library = library;
        this.message = message;
        this.numDirectDifference = 0;
        this.numTransitiveDifference = 0;
        this.onlyByAnalysisDirect = new ArrayList<>();
        this.onlyByPaperDirect = new ArrayList<>();
        this.onlyByAnalysisTransitive = new ArrayList<>();
        this.onlyByPaperTransitive = new ArrayList<>();
    }

    public String getLibrary() {
        return library;
    }

    public String getMessage() {
        return message;
    }

    public Integer getNumDirectDifference() {
        return numDirectDifference;
    }

    public Integer getNumTransitiveDifference() {
        return numTransitiveDifference;
    }

    public List<String> getOnlyByAnalysisDirect() {
        return onlyByAnalysisDirect;
    }

    public List<String> getOnlyByPaperDirect() {
        return onlyByPaperDirect;
    }

    public List<String> getOnlyByAnalysisTransitive() {
        return onlyByAnalysisTransitive;
    }

    public List<String> getOnlyByPaperTransitive() {
        return onlyByPaperTransitive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Difference that = (Difference) o;
        return library.equals(that.library);
    }

    @Override
    public int hashCode() {
        return Objects.hash(library);
    }
}
