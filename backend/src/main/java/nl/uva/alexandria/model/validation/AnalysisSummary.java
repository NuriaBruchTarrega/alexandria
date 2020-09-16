package nl.uva.alexandria.model.validation;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.Library;

import java.util.*;

public class AnalysisSummary {

    private final String groupId;
    private final String artifactId;
    private final String version;
    private Map<String, Boolean> isCouplingEnoughMap = new HashMap<>();
    private Set<String> couplingNotEnoughLibraries = new HashSet<>();

    private AnalysisSummary(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public static AnalysisSummary from(DependencyTreeResult result) {
        Library clientLibrary = result.getLibrary();
        AnalysisSummary newAnalysisSummary = new AnalysisSummary(clientLibrary.getGroupID(), clientLibrary.getArtifactID(), clientLibrary.getVersion());

        Queue<DependencyTreeResult> toVisit = new ArrayDeque<>(result.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeResult visiting = toVisit.poll();
            boolean isCouplingEnough =
                    !visiting.getAcAtDistance().isEmpty() || !visiting.getMicAtDistance().isEmpty()
                            || (visiting.getNumReachableClasses() == 0 && visiting.getNumReachableBehaviors() == 0);
            newAnalysisSummary.putToIsCouplingEnoughMap(visiting.getLibrary().toString(), isCouplingEnough);

            if (!isCouplingEnough) newAnalysisSummary.addToCouplingNotEnoughLibraries(visiting.getLibrary().toString());

            toVisit.addAll(visiting.getChildren());
        }

        return newAnalysisSummary;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, Boolean> getIsCouplingEnoughMap() {
        return isCouplingEnoughMap;
    }

    public Set<String> getCouplingNotEnoughLibraries() {
        return couplingNotEnoughLibraries;
    }

    private void putToIsCouplingEnoughMap(String library, boolean isCouplingEnough) {
        this.isCouplingEnoughMap.put(library, isCouplingEnough);
    }

    private void addToCouplingNotEnoughLibraries(String library) {
        this.couplingNotEnoughLibraries.add(library);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisSummary that = (AnalysisSummary) o;
        return groupId.equals(that.groupId) &&
                artifactId.equals(that.artifactId) &&
                version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
