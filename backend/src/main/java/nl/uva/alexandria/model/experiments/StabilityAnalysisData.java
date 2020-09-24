package nl.uva.alexandria.model.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.*;

public class StabilityAnalysisData {
    private final String clientLibrary;
    private final String serverLibrary;
    private final Map<Integer, Integer> micAtDistance;
    private final Map<Integer, Integer> acAtDistance;
    private Map<Double, Double> micStabilityAnalysisData = new HashMap<>();
    private Map<Double, Double> acStabilityAnalysisData = new HashMap<>();

    private StabilityAnalysisData(String clientLibrary, String serverLibrary, Map<Integer, Integer> micAtDistance, Map<Integer, Integer> acAtDistance) {
        this.clientLibrary = clientLibrary;
        this.serverLibrary = serverLibrary;
        this.micAtDistance = micAtDistance;
        this.acAtDistance = acAtDistance;
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
                StabilityAnalysisData stabilityAnalysisData = new StabilityAnalysisData(clientLibrary, visiting.getLibrary().toString(), visiting.getMicAtDistance(), visiting.getAcAtDistance());
                stabilityAnalysisDataSet.add(stabilityAnalysisData);
                toVisit.addAll(visiting.getChildren());
            }
        }

        return stabilityAnalysisDataSet;
    }

    public void runStabilityAnalysis() {
        runStabilityAnalysis(this.micAtDistance, this.micStabilityAnalysisData);
        runStabilityAnalysis(this.acAtDistance, this.acStabilityAnalysisData);
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

    private void runStabilityAnalysis(Map<Integer, Integer> metricAtDistance, Map<Double, Double> metricStabilityAnalysisData) {
        double propagationFactor = 0.01;

        while (propagationFactor <= 1) {
            double metric = 0;

            for (Map.Entry<Integer, Integer> entry : metricAtDistance.entrySet()) {
                Integer distance = entry.getKey();
                Integer value = entry.getValue();
                metric += value * Math.pow(propagationFactor, distance - 1);
            }
            metricStabilityAnalysisData.put(propagationFactor, metric);

            propagationFactor += 0.01;
        }
    }
}
