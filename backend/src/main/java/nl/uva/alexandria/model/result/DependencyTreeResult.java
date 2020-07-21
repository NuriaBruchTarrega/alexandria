package nl.uva.alexandria.model.result;

import nl.uva.alexandria.model.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyTreeResult {
    private Library library;
    private Map<Integer, Integer> micAtDistance;
    private Map<Integer, Integer> acAtDistance;
    private ClassDistribution micClassDistribution;
    private ClassDistribution acClassDistribution;

    private List<DependencyTreeResult> children;

    public DependencyTreeResult(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.micAtDistance = new HashMap<>();
        this.acAtDistance = new HashMap<>();
        this.micClassDistribution = new ClassDistribution();
        this.acClassDistribution = new ClassDistribution();
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

    public ClassDistribution getMicClassDistribution() {
        return micClassDistribution;
    }

    public ClassDistribution getAcClassDistribution() {
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
        this.micClassDistribution.addConnectionFromClass(className);
    }

    public void addAcConnectionFromClass(String className) {
        this.acClassDistribution.addConnectionFromClass(className);
    }
}
