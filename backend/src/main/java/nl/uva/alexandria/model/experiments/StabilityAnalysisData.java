package nl.uva.alexandria.model.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.*;

public class StabilityAnalysisData {
    private final String clientLibrary;
    private final String serverLibrary;
    private Map<Integer, Integer> micAtDistance = new HashMap<>();
    private Map<Integer, Integer> acAtDistance = new HashMap<>();
    private Map<Float, Float> micStabilityAnalysisData = new HashMap<>();
    private Map<Float, Float> acStabilityAnalysisData = new HashMap<>();

    private StabilityAnalysisData(String clientLibrary, String serverLibrary) {
        this.clientLibrary = clientLibrary;
        this.serverLibrary = serverLibrary;
    }

    public static Set<StabilityAnalysisData> from(DependencyTreeResult dependencyTreeResult) {
        Set<StabilityAnalysisData> stabilityAnalysisDataSet = new HashSet<>();
        // TODO: implementation
        return stabilityAnalysisDataSet;
    }

    public void setMicAtDistance(Map<Integer, Integer> micAtDistance) {
        this.micAtDistance = micAtDistance;
    }

    public void setAcAtDistance(Map<Integer, Integer> acAtDistance) {
        this.acAtDistance = acAtDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StabilityAnalysisData that = (StabilityAnalysisData) o;
        return clientLibrary.equals(that.clientLibrary) &&
                serverLibrary.equals(that.serverLibrary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientLibrary, serverLibrary);
    }
}
