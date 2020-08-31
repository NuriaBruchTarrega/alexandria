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
    private int numClasses = 0;
    private int numBehaviors = 0;
    private int numReachableClasses = 0;
    private int numReachableBehaviors = 0;

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

    public int getNumClasses() {
        return numClasses;
    }

    public int getNumBehaviors() {
        return numBehaviors;
    }

    public int getNumReachableClasses() {
        return numReachableClasses;
    }

    public int getNumReachableBehaviors() {
        return numReachableBehaviors;
    }

    public void setNumClasses(int numClasses) {
        this.numClasses = numClasses;
    }

    public void setNumBehaviors(int numBehaviors) {
        this.numBehaviors = numBehaviors;
    }

    public void setNumReachableClasses(int numReachableClasses) {
        this.numReachableClasses = numReachableClasses;
    }

    public void setNumReachableBehaviors(int numReachableBehaviors) {
        this.numReachableBehaviors = numReachableBehaviors;
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

    public boolean isUsed() {
        if (numReachableBehaviors != 0 || numReachableClasses != 0) return false;
        else if (children.isEmpty()) return true;
        return children.stream().allMatch(DependencyTreeResult::isUsed);
    }
}
