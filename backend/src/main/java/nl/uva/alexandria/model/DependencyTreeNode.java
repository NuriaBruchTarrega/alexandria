package nl.uva.alexandria.model;

import javassist.CtBehavior;
import org.springframework.lang.Nullable;

import java.util.*;

public class DependencyTreeNode {

    private Library library;
    private Map<CtBehavior, Integer> reachableApiBehaviors;
    private List<DependencyTreeNode> children;

    public DependencyTreeNode(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.reachableApiBehaviors = new HashMap<>();
    }

    public Library getLibrary() {
        return library;
    }

    public List<DependencyTreeNode> getChildren() {
        return new ArrayList<>(this.children);
    }

    public Set<CtBehavior> getReachableApiBehaviors() {
        return new HashSet<>(this.reachableApiBehaviors.keySet());
    }

    public void addChild(DependencyTreeNode child) {
        this.children.add(child);
    }

    public void addReachableApiBehaviorCall(CtBehavior behavior) {
        this.reachableApiBehaviors.computeIfPresent(behavior, (key, value) -> value + 1);
        this.reachableApiBehaviors.putIfAbsent(behavior, 1);
    }

    @Nullable
    public DependencyTreeNode findLibraryNode(Library library) {
        if (this.library == library) return this;
        Queue<DependencyTreeNode> toVisit = new LinkedList<>(this.children);

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            if (visiting.library == library) return visiting;
            toVisit.addAll(visiting.children);
        }

        return null;
    }
}
