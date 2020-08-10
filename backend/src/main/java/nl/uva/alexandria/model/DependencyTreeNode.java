package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.expr.Expr;

import java.util.*;

public class DependencyTreeNode {

    private Library library;
    private Map<Integer, ReachableBehaviors> reachableBehaviorsAtDistance;
    private Map<Integer, ReachableClasses> reachableClassesAtDistance;
    private Map<Integer, ReachableAnnotations> reachableAnnotationsAtDistance;

    private List<DependencyTreeNode> children;

    public DependencyTreeNode(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.reachableBehaviorsAtDistance = new HashMap<>();
        this.reachableClassesAtDistance = new HashMap<>();
    }

    public Library getLibrary() {
        return library;
    }

    public List<DependencyTreeNode> getChildren() {
        return this.children;
    }

    public Map<Integer, ReachableBehaviors> getReachableBehaviorsAtDistance() {
        return reachableBehaviorsAtDistance;
    }

    public Map<Integer, ReachableClasses> getReachableClassesAtDistance() {
        return reachableClassesAtDistance;
    }

    public void addChild(DependencyTreeNode child) {
        this.children.add(child);
    }

    public void addReachableApiBehavior(Integer distance, CtBehavior behavior, Set<Expr> reachableFrom) {
        this.reachableBehaviorsAtDistance.putIfAbsent(distance, new ReachableBehaviors());
        ReachableBehaviors reachableBehaviors = this.reachableBehaviorsAtDistance.get(distance);
        reachableBehaviors.addReachableMethod(behavior, reachableFrom);
    }

    public void addReachableApiClass(Integer distance, CtClass ctClass, Set<CtField> declarations) {
        this.reachableClassesAtDistance.putIfAbsent(distance, new ReachableClasses());
        ReachableClasses reachableClasses = this.reachableClassesAtDistance.get(distance);
        reachableClasses.addReachableClass(ctClass, declarations);
    }

    public Optional<DependencyTreeNode> findLibraryNode(Library library) {
        if (this.library.equals(library)) return Optional.of(this);
        Queue<DependencyTreeNode> toVisit = new ArrayDeque<>(this.children);

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            if (visiting.getLibrary().equals(library)) return Optional.of(visiting);
            toVisit.addAll(visiting.children);
        }

        return Optional.empty();
    }

    public void addReachableAnnotationClass(Integer distance, CtClass annotationClass, Integer numUsages) {
        this.reachableAnnotationsAtDistance.putIfAbsent(distance, new ReachableAnnotations());
        ReachableAnnotations reachableAnnotations = this.reachableAnnotationsAtDistance.get(distance);
        reachableAnnotations.addReachableAnnotation(annotationClass, numUsages);
    }
}
