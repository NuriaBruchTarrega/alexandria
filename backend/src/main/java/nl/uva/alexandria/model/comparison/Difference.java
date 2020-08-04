package nl.uva.alexandria.model.comparison;

import java.util.List;
import java.util.Objects;

public class Difference {
    private String library;
    private Integer numDetectedDifference;
    private List<String> detectedOnlyByAnalysis;
    private List<String> detectedOnlyByPaper;

    public Difference(String library, Integer numDetectedDifference, List<String> detectedOnlyByAnalysis, List<String> detectedOnlyByPaper) {
        this.library = library;
        this.numDetectedDifference = numDetectedDifference;
        this.detectedOnlyByAnalysis = detectedOnlyByAnalysis;
        this.detectedOnlyByPaper = detectedOnlyByPaper;
    }

    public String getLibrary() {
        return library;
    }

    public Integer getNumDetectedDifference() {
        return numDetectedDifference;
    }

    public List<String> getDetectedOnlyByAnalysis() {
        return detectedOnlyByAnalysis;
    }

    public List<String> getDetectedOnlyByPaper() {
        return detectedOnlyByPaper;
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
