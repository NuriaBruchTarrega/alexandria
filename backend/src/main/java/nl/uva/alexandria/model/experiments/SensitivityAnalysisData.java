package nl.uva.alexandria.model.experiments;

import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.*;

public class SensitivityAnalysisData {
    private final String clientLibrary;
    private final String serverLibrary;
    private final Map<Integer, Integer> micAtDistance;
    private final Map<Integer, Integer> acAtDistance;
    private Map<Double, Double> micSensitivityAnalysisData = new HashMap<>();
    private Map<Double, Double> acSensitivityAnalysisData = new HashMap<>();

    private SensitivityAnalysisData(String clientLibrary, String serverLibrary, Map<Integer, Integer> micAtDistance, Map<Integer, Integer> acAtDistance) {
        this.clientLibrary = clientLibrary;
        this.serverLibrary = serverLibrary;
        this.micAtDistance = micAtDistance;
        this.acAtDistance = acAtDistance;
    }

    public static Set<SensitivityAnalysisData> from(DependencyTreeResult dependencyTreeResult) {
        String clientLibrary = dependencyTreeResult.getLibrary().toString();
        Set<SensitivityAnalysisData> sensitivityAnalysisDataSet = new HashSet<>();

        List<DependencyTreeResult> directDependencies = dependencyTreeResult.getChildren();
        Queue<DependencyTreeResult> toVisit = new ArrayDeque<>();
        directDependencies.forEach(directDependency -> toVisit.addAll(directDependency.getChildren()));

        while (!toVisit.isEmpty()) {
            DependencyTreeResult visiting = toVisit.poll();
            if (checkHasCoupling(visiting)) {
                SensitivityAnalysisData sensitivityAnalysisData = new SensitivityAnalysisData(clientLibrary, visiting.getLibrary().toString(), visiting.getMicAtDistance(), visiting.getAcAtDistance());
                sensitivityAnalysisDataSet.add(sensitivityAnalysisData);
                toVisit.addAll(visiting.getChildren());
            }
        }

        return sensitivityAnalysisDataSet;
    }

    public void runSensitivityAnalysis() {
        runSensitivityAnalysis(this.micAtDistance, this.micSensitivityAnalysisData);
        runSensitivityAnalysis(this.acAtDistance, this.acSensitivityAnalysisData);
    }

    public String getClientLibrary() {
        return clientLibrary;
    }

    public String getServerLibrary() {
        return serverLibrary;
    }

    public Map<Integer, Integer> getMicAtDistance() {
        return micAtDistance;
    }

    public Map<Integer, Integer> getAcAtDistance() {
        return acAtDistance;
    }

    public Map<Double, Double> getMicSensitivityAnalysisData() {
        return micSensitivityAnalysisData;
    }

    public Map<Double, Double> getAcSensitivityAnalysisData() {
        return acSensitivityAnalysisData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensitivityAnalysisData that = (SensitivityAnalysisData) o;
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

    private void runSensitivityAnalysis(Map<Integer, Integer> metricAtDistance, Map<Double, Double> metricSensitivityAnalysisData) {
        double propagationFactor = 0.01;

        while (propagationFactor <= 1) {
            double metric = 0;

            for (Map.Entry<Integer, Integer> entry : metricAtDistance.entrySet()) {
                Integer distance = entry.getKey();
                Integer value = entry.getValue();
                metric += value * Math.pow(propagationFactor, Double.valueOf(distance) - 1);
            }
            metricSensitivityAnalysisData.put(propagationFactor, metric);

            propagationFactor += 0.01;
        }
    }
}
