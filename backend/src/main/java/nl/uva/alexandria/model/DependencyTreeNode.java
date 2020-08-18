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
    private Map<Integer, Set<CtBehavior>> reachableBehaviorsAtDistance;
    private Map<Integer, Set<CtClass>> reachableClassesAtDistance;
    private int numClasses = 0;
    private int numBehaviors = 0;
    private int numReachableClasses = 0;
    private int numReachableBehaviors = 0;

    private List<DependencyTreeNode> children;

    public DependencyTreeNode(Library library) {
        this.library = library;
        this.children = new ArrayList<>();
        this.reachableApiBehaviorsAtDistance = new HashMap<>();
        this.reachableApiFieldClassesAtDistance = new HashMap<>();
        this.reachableAnnotationsAtDistance = new HashMap<>();
        this.reachableBehaviorsAtDistance = new HashMap<>();
        this.reachableClassesAtDistance = new HashMap<>();
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

    public Map<Integer, Set<CtBehavior>> getReachableBehaviorsAtDistance() {
        return reachableBehaviorsAtDistance;
    }

    public Map<Integer, Set<CtClass>> getReachableClassesAtDistance() {
        return reachableClassesAtDistance;
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

    public void addChild(DependencyTreeNode child) {
        this.children.add(child);
    }

    public void addReachableApiBehavior(int distance, CtBehavior behavior, Set<Expr> reachableFrom) {
        this.reachableApiBehaviorsAtDistance.putIfAbsent(distance, new ReachableBehaviors());
        ReachableBehaviors reachableBehaviorsObj = this.reachableApiBehaviorsAtDistance.get(distance);
        reachableBehaviorsObj.addReachableMethod(behavior, reachableFrom);
        this.addReachableBehavior(behavior, distance);
    }

    public void addReachableApiFieldClass(Integer distance, CtClass ctClass, Set<CtField> declarations) {
        this.reachableApiFieldClassesAtDistance.putIfAbsent(distance, new ReachableClasses());
        ReachableClasses reachableClassesObj = this.reachableApiFieldClassesAtDistance.get(distance);
        reachableClassesObj.addReachableClass(ctClass, declarations);
        this.addReachableClass(ctClass, distance);
    }

    public void addReachableBehavior(CtBehavior ctBehavior, int distance) {
        this.reachableBehaviorsAtDistance.putIfAbsent(distance, new HashSet<>());
        this.reachableBehaviorsAtDistance.get(distance).add(ctBehavior);
    }

    public void addReachableClass(CtClass ctClass, int distance) {
        this.reachableClassesAtDistance.putIfAbsent(distance, new HashSet<>());
        this.reachableClassesAtDistance.get(distance).add(ctClass);
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
        addReachableClass(annotationClass, distance);
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
