package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.expr.MethodCall;

import java.util.*;

public class DependencyTreeNode {

    private Library library;
    private Map<Integer, ReachableMethods> reachableMethodsAtDistance;
    private Map<Integer, ReachableFields> reachableFieldsAtDistance;

    private List<DependencyTreeNode> children;

    public DependencyTreeNode(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.reachableMethodsAtDistance = new HashMap<>();
        this.reachableFieldsAtDistance = new HashMap<>();
    }

    public Library getLibrary() {
        return library;
    }

    public List<DependencyTreeNode> getChildren() {
        return this.children;
    }

    public Map<Integer, ReachableMethods> getReachableMethodsAtDistance() {
        return reachableMethodsAtDistance;
    }

    public Map<Integer, ReachableFields> getReachableFieldsAtDistance() {
        return reachableFieldsAtDistance;
    }

    public void addChild(DependencyTreeNode child) {
        this.children.add(child);
    }

    public void addReachableApiBehavior(Integer distance, CtBehavior behavior, Set<MethodCall> reachableFrom) {
        this.reachableMethodsAtDistance.putIfAbsent(distance, new ReachableMethods());
        ReachableMethods reachableMethods = this.reachableMethodsAtDistance.get(distance);
        reachableMethods.addReachableMethod(behavior, reachableFrom);
    }

    public void addReachableApiField(Integer distance, CtClass ctClass, Set<CtField> declarations) {
        this.reachableFieldsAtDistance.putIfAbsent(distance, new ReachableFields());
        ReachableFields reachableFields = this.reachableFieldsAtDistance.get(distance);
        reachableFields.addReachableClass(ctClass, declarations);
    }

    public Optional<DependencyTreeNode> findLibraryNode(Library library) {
        if (this.library.equals(library)) return Optional.of(this);
        Queue<DependencyTreeNode> toVisit = new LinkedList<>(this.children);

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            if (visiting.getLibrary().equals(library)) return Optional.of(visiting);
            toVisit.addAll(visiting.children);
        }

        return Optional.empty();
    }
}
