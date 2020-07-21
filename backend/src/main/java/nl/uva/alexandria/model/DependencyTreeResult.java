package nl.uva.alexandria.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyTreeResult {
    private Library library;
    private Map<Integer, Integer> micAtDistance;
    private Map<Integer, Integer> acAtDistance;
    private Map<String, Integer> micClassDistribution;
    private Map<String, Integer> acClassDistribution;

    private List<DependencyTreeResult> children;

    public DependencyTreeResult(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.micAtDistance = new HashMap<>();
        this.acAtDistance = new HashMap<>();
        this.micClassDistribution = new HashMap<>();
        this.acClassDistribution = new HashMap<>();
    }

    public Library getLibrary() {
        return library;
    }

    public List<DependencyTreeResult> getChildren() {
        return children;
    }

    public Map<Integer, Integer> getMicAtDistance() {
        return micAtDistance;
    }

    public Map<Integer, Integer> getAcAtDistance() {
        return acAtDistance;
    }

    public Map<String, Integer> getMicClassDistribution() {
        return micClassDistribution;
    }

    public Map<String, Integer> getAcClassDistribution() {
        return acClassDistribution;
    }

    public void addChildren(DependencyTreeResult child) {
        this.children.add(child);
    }

    public void addMicAtDistance(Integer distance, Integer mic) {
        this.micAtDistance.put(distance, mic);
    }

    public void addAcAtDistance(Integer distance, Integer ac) {
        this.acAtDistance.put(distance, ac);
    }

    public void addMicConnectionFromClass(String className) {
        this.micClassDistribution.computeIfPresent(className, (key, value) -> value + 1);
        this.micClassDistribution.putIfAbsent(className, 1);
    }

    public void addAcConnectionFromClass(String className) {
        this.acClassDistribution.computeIfPresent(className, (key, value) -> value + 1);
        this.acClassDistribution.putIfAbsent(className, 1);
    }
}
