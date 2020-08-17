package nl.uva.alexandria.logic.metrics;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;

import java.util.*;

public class DependencyUsage {

    private DependencyUsage() {
    }

    public static void calculateDependencyUsage(DependencyTreeNode rootNode, ClassPoolManager classPoolManager) {
        Queue<DependencyTreeNode> toVisit = new ArrayDeque<>(rootNode.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            toVisit.addAll(visiting.getChildren());

            // Calculate total of behaviors and classes
            try {
                calculateNumOfClassesAndBehaviors(visiting, classPoolManager);
            } catch (NotFoundException e) {
                // TODO: LOG warn no library classes found
            }
            // Calculate number of reachable classes and behaviors
            calculateNumOfUsedClassesAndBehaviors(visiting);
        }
    }

    private static void calculateNumOfClassesAndBehaviors(DependencyTreeNode dependencyTreeNode, ClassPoolManager classPoolManager) throws NotFoundException {
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(dependencyTreeNode.getLibrary().getLibraryPath());
        dependencyTreeNode.setNumClasses(libraryClasses.size());
        Integer numBehaviors = libraryClasses.stream().map(libraryClass -> libraryClass.getDeclaredBehaviors().length).reduce(0, Integer::sum);
        dependencyTreeNode.setNumBehaviors(numBehaviors);
    }

    private static void calculateNumOfUsedClassesAndBehaviors(DependencyTreeNode dependencyTreeNode) {
        Map<Integer, Set<CtClass>> reachableClassesAtDistance = dependencyTreeNode.getReachableClassesAtDistance();
        Set<CtClass> allReachableClasses = new HashSet<>();
        reachableClassesAtDistance.forEach((distance, reachableClassesSet) -> allReachableClasses.addAll(reachableClassesSet));
        dependencyTreeNode.setNumReachableClasses(allReachableClasses.size());

        Map<Integer, Set<CtBehavior>> reachableBehaviorsAtDistance = dependencyTreeNode.getReachableBehaviorsAtDistance();
        Set<CtBehavior> allReachableBehaviors = new HashSet<>();
        reachableBehaviorsAtDistance.forEach((distance, reachableBehaviorsSet) -> allReachableBehaviors.addAll(reachableBehaviorsSet));
        dependencyTreeNode.setNumReachableBehaviors(allReachableBehaviors.size());
    }
}
