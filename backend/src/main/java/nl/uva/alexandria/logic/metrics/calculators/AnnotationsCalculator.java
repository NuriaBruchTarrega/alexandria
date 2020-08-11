package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.DescendantsDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableBehaviors;
import nl.uva.alexandria.model.ReachableClasses;
import nl.uva.alexandria.model.factories.LibraryFactory;

import java.lang.annotation.Annotation;
import java.util.*;

public class AnnotationsCalculator extends MetricCalculator {

    public AnnotationsCalculator(ClassPoolManager classPoolManager) {
        super(classPoolManager, new DescendantsDetector(classPoolManager));
    }

    @Override
    public void calculateMetric(DependencyTreeNode dependencyTreeNode) {
        this.rootLibrary = dependencyTreeNode;
        calculateDirectCoupling();
        iterateTree();
    }

    /* Direct coupling */
    private void calculateDirectCoupling() {
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();

        clientClasses.forEach(clientClass -> {
            //TODO: catch annotations in fields and in variables
            try {
                Object[] annotations = clientClass.getAvailableAnnotations();
                computeFoundAnnotations(annotations, 1, this.rootLibrary);
            } catch (NoClassDefFoundError e1) {
                LOG.info("No class definition: {}", e1.getMessage());
            }

            CtBehavior[] behaviors = clientClass.getDeclaredBehaviors();
            for (CtBehavior behavior : behaviors) {
                try {
                    Object[] annotations = behavior.getAvailableAnnotations();
                    computeFoundAnnotations(annotations, 1, this.rootLibrary);
                } catch (NoClassDefFoundError e1) {
                    LOG.info("No class definition: {}", e1.getMessage());
                }
            }
        });
    }

    /* Transitive coupling */

    private void iterateTree() {
        Queue<DependencyTreeNode> toVisit = new ArrayDeque<>(this.rootLibrary.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            if (visiting.getChildren().isEmpty()) continue;
            toVisit.addAll(visiting.getChildren());

            if (visiting.getReachableBehaviorsAtDistance().isEmpty() && visiting.getReachableClassesAtDistance().isEmpty())
                continue;
            calculateTransitiveCoupling(visiting);
        }
    }

    private void calculateTransitiveCoupling(DependencyTreeNode currentLibrary) {
        // Find annotations in reachable classes
        Map<Integer, ReachableClasses> reachableClassesAtDistance = currentLibrary.getReachableClassesAtDistance();
        reachableClassesAtDistance.forEach((distance, reachableClasses) -> {
            Set<CtClass> reachableClassesSet = reachableClasses.getReachableClassesMap().keySet();
            reachableClassesSet.forEach(reachableClass -> {
                // TODO: find annotations in fields
                Object[] annotations = reachableClass.getAvailableAnnotations();
                computeFoundAnnotations(annotations, distance + 1, currentLibrary);
            });
        });

        // Find annotations in reachable methods
        Map<Integer, ReachableBehaviors> reachableBehaviorsAtDistance = currentLibrary.getReachableBehaviorsAtDistance();
        reachableBehaviorsAtDistance.forEach((distance, reachableBehaviors) -> {
            Set<CtBehavior> reachableBehaviorsSet = reachableBehaviors.getReachableBehaviorsMap().keySet();
            reachableBehaviorsSet.forEach(reachableBehavior -> {
                Object[] annotations = reachableBehavior.getAvailableAnnotations();
                computeFoundAnnotations(annotations, distance + 1, currentLibrary);
            });
        });
    }

    /* Shared for direct and transitive */

    private void computeFoundAnnotations(Object[] annotations, Integer distance, DependencyTreeNode currentLibrary) {
        if (annotations.length == 0) return;

        for (Object annotation : annotations) {
            String annotationName = ((Annotation) annotation).annotationType().getName();
            try {
                CtClass annotationClass = classPoolManager.getClassFromClassName(annotationName);
                if (classPoolManager.isClassInDependency(annotationClass, currentLibrary.getLibrary().getLibraryPath())) {
                    addReachableAnnotation(annotationClass, distance, 1);
                }
            } catch (NotFoundException e) {
                LOG.info("Annotation class not found: {}", e.getMessage());
            }
        }
    }

    private void addReachableAnnotation(CtClass annotationClass, Integer distance, Integer numUsages) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(annotationClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableAnnotationClass(distance, annotationClass, numUsages);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }
}
