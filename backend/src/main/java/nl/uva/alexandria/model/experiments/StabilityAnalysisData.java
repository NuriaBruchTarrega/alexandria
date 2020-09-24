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
        String clientLibrary = dependencyTreeResult.getLibrary().toString();
        Set<StabilityAnalysisData> stabilityAnalysisDataSet = new HashSet<>();

        List<DependencyTreeResult> directDependencies = dependencyTreeResult.getChildren();
        Queue<DependencyTreeResult> toVisit = new ArrayDeque<>();
        directDependencies.forEach(directDependency -> toVisit.addAll(directDependency.getChildren()));

        while (!toVisit.isEmpty()) {
            DependencyTreeResult visiting = toVisit.poll();
            if (checkHasCoupling(visiting)) {
                StabilityAnalysisData stabilityAnalysisData = new StabilityAnalysisData(clientLibrary, visiting.getLibrary().toString());
                stabilityAnalysisData.setMicAtDistance(visiting.getMicAtDistance());
                stabilityAnalysisData.setAcAtDistance(visiting.getAcAtDistance());
                stabilityAnalysisDataSet.add(stabilityAnalysisData);
                toVisit.addAll(visiting.getChildren());
            }
        }

        return stabilityAnalysisDataSet;
    }

    private static boolean checkHasCoupling(DependencyTreeResult visiting) {
        Map<Integer, Integer> micAtDistance = visiting.getMicAtDistance();
        Map<Integer, Integer> acAtDistance = visiting.getAcAtDistance();
        if (micAtDistance.size() > 1) return true;
        if (acAtDistance.size() > 1) return true;

        Set<Integer> micKeys = micAtDistance.keySet();
        Set<Integer> acKeys = acAtDistance.keySet();

        if (micKeys.isEmpty() && acKeys.isEmpty()) return false;

        return !micKeys.contains(1) || !acKeys.contains(1);
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
