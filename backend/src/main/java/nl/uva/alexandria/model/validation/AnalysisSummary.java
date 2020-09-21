package nl.uva.alexandria.model.validation;

import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.Library;

import java.util.*;

public class AnalysisSummary {

    private final String groupId;
    private final String artifactId;
    private final String version;
    private Map<String, Boolean> isCouplingEnoughMap = new HashMap<>();
    private Map<String, Boolean> isMicEnoughMap = new HashMap<>();
    private Map<String, Boolean> isAcEnoughMap = new HashMap<>();
    private Set<String> couplingNotEnoughLibraries = new HashSet<>();
    private Set<String> micNotEnoughLibraries = new HashSet<>();
    private Set<String> acNotEnoughLibraries = new HashSet<>();

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
            String libraryName = visiting.getLibrary().toString();
            boolean isCouplingEnough =
                    !visiting.getAcAtDistance().isEmpty() || !visiting.getMicAtDistance().isEmpty()
                            || (visiting.getNumReachableClasses() == 0 && visiting.getNumReachableBehaviors() == 0);
            boolean isMicEnough = !visiting.getMicAtDistance().isEmpty() || (visiting.getNumReachableClasses() == 0 && visiting.getNumReachableBehaviors() == 0);
            boolean isAcEnough = !visiting.getMicAtDistance().isEmpty() || (visiting.getNumReachableClasses() == 0 && visiting.getNumReachableBehaviors() == 0);

            newAnalysisSummary.putToIsCouplingEnoughMap(libraryName, isCouplingEnough);
            newAnalysisSummary.putToIsMicEnoughMap(libraryName, isMicEnough);
            newAnalysisSummary.putToIsAcEnoughMap(libraryName, isAcEnough);

            if (!isCouplingEnough) newAnalysisSummary.addToCouplingNotEnoughLibraries(libraryName);
            if (!isMicEnough) newAnalysisSummary.addToMicNotEnoughLibraries(libraryName);
            if (!isCouplingEnough) newAnalysisSummary.addToAcNotEnoughLibraries(libraryName);

            toVisit.addAll(visiting.getChildren());
        }

        return newAnalysisSummary;
    }

    public Map<String, Boolean> getIsCouplingEnoughMap() {
        return isCouplingEnoughMap;
    }

    public Map<String, Boolean> getIsMicEnoughMap() {
        return isMicEnoughMap;
    }

    public Map<String, Boolean> getIsAcEnoughMap() {
        return isAcEnoughMap;
    }

    public Set<String> getCouplingNotEnoughLibraries() {
        return couplingNotEnoughLibraries;
    }

    public Set<String> getMicNotEnoughLibraries() {
        return micNotEnoughLibraries;
    }

    public Set<String> getAcNotEnoughLibraries() {
        return acNotEnoughLibraries;
    }

    private void putToIsCouplingEnoughMap(String library, boolean isCouplingEnough) {
        this.isCouplingEnoughMap.put(library, isCouplingEnough);
    }

    private void putToIsMicEnoughMap(String library, boolean isMicEnough) {
        this.isMicEnoughMap.put(library, isMicEnough);
    }

    private void putToIsAcEnoughMap(String library, boolean isAcEnough) {
        this.isAcEnoughMap.put(library, isAcEnough);
    }

    private void addToCouplingNotEnoughLibraries(String library) {
        this.couplingNotEnoughLibraries.add(library);
    }

    private void addToMicNotEnoughLibraries(String library) {
        this.micNotEnoughLibraries.add(library);
    }

    private void addToAcNotEnoughLibraries(String library) {
        this.acNotEnoughLibraries.add(library);
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
