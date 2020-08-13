package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.expr.Expr;

import java.util.*;

public class DependencyTreeNode {

    private Library library;
    private Map<Integer, ReachableBehaviors> reachableApiBehaviorsAtDistance;
    private Map<Integer, ReachableClasses> reachableApiFieldClassesAtDistance;
    private Map<Integer, ReachableAnnotations> reachableAnnotationsAtDistance;
    private Set<CtBehavior> reachableBehaviors;
    private Set<CtClass> reachableClasses;

    private List<DependencyTreeNode> children;

    public DependencyTreeNode(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.reachableApiBehaviorsAtDistance = new HashMap<>();
        this.reachableApiFieldClassesAtDistance = new HashMap<>();
        this.reachableAnnotationsAtDistance = new HashMap<>();
        this.reachableBehaviors = new HashSet<>();
        this.reachableClasses = new HashSet<>();
    }

    public Library getLibrary() {
        return library;
    }

    public List<DependencyTreeNode> getChildren() {
        return this.children;
    }

    public Map<Integer, ReachableBehaviors> getReachableApiBehaviorsAtDistance() {
        return reachableApiBehaviorsAtDistance;
    }

    public Map<Integer, ReachableClasses> getReachableApiFieldClassesAtDistance() {
        return reachableApiFieldClassesAtDistance;
    }

    public Map<Integer, ReachableAnnotations> getReachableAnnotationsAtDistance() {
        return reachableAnnotationsAtDistance;
    }

    public Set<CtBehavior> getReachableBehaviors() {
        return reachableBehaviors;
    }

    public Set<CtClass> getReachableClasses() {
        return reachableClasses;
    }

    public void addChild(DependencyTreeNode child) {
        this.children.add(child);
    }

    public void addReachableApiBehavior(Integer distance, CtBehavior behavior, Set<Expr> reachableFrom) {
        this.reachableApiBehaviorsAtDistance.putIfAbsent(distance, new ReachableBehaviors());
        ReachableBehaviors reachableBehaviors = this.reachableApiBehaviorsAtDistance.get(distance);
        reachableBehaviors.addReachableMethod(behavior, reachableFrom);
    }

    public void addReachableApiFieldClass(Integer distance, CtClass ctClass, Set<CtField> declarations) {
        this.reachableApiFieldClassesAtDistance.putIfAbsent(distance, new ReachableClasses());
        ReachableClasses reachableClasses = this.reachableApiFieldClassesAtDistance.get(distance);
        reachableClasses.addReachableClass(ctClass, declarations);
    }

    public void addReachableBehavior(CtBehavior ctBehavior) {
        this.reachableBehaviors.add(ctBehavior);
    }

    public void addReachableClass(CtClass ctClass) {
        this.reachableClasses.add(ctClass);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyTreeNode that = (DependencyTreeNode) o;
        return library.equals(that.library);
    }

    @Override
    public int hashCode() {
        return Objects.hash(library);
    }
}
