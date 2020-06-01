package nl.uva.alexandria.model;

import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyTreeNode {

    private Library library;
    private Set<CtBehavior> reachableApiBehaviors;
    private List<DependencyTreeNode> children;

    public DependencyTreeNode(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.reachableApiBehaviors = new HashSet<>();
    }

    public Library getLibrary() {
        return library;
    }

    public List<DependencyTreeNode> getChildren() {
        return new ArrayList<>(this.children);
    }

    public Set<CtBehavior> getReachableApiBehaviors() {
        return new HashSet<>(this.reachableApiBehaviors);
    }

    public void addChild(DependencyTreeNode child) {
        this.children.add(child);
    }

    public void addReachableApiBehavior(CtBehavior behavior) {
        this.reachableApiBehaviors.add(behavior);
    }
}
