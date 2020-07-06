package nl.uva.alexandria.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyTreeResult {
    private Library library;
    private Map<Integer, Integer> micAtDistance;
    private Map<Integer, Integer> acAtDistance;

    private List<DependencyTreeResult> children;

    public DependencyTreeResult(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.micAtDistance = new HashMap<>();
        this.acAtDistance = new HashMap<>();
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

    public void addChildren(DependencyTreeResult child) {
        this.children.add(child);
    }

    public void addMicAtDistance(Integer distance, Integer mic) {
        this.micAtDistance.put(distance, mic);
    }

    public void addAcAtDistance(Integer distance, Integer ac) {
        this.acAtDistance.put(distance, ac);
    }
}
