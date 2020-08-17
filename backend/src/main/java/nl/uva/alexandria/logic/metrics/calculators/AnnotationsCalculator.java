package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.DescendantsDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableBehaviors;
import nl.uva.alexandria.model.ReachableClasses;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class AnnotationsCalculator extends MetricCalculator {

    public AnnotationsCalculator(ClassPoolManager classPoolManager, DependencyTreeNode rootLibrary) {
        super(classPoolManager, new DescendantsDetector(classPoolManager), rootLibrary);
    }

    // PUBLIC METHODS
    @Override
    public void visitClientLibrary() {
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();

        clientClasses.forEach(clientClass -> {
            findAnnotations(clientClass, 1, this.rootLibrary);

            CtField[] fields = clientClass.getDeclaredFields();
            for (CtField field : fields) findAnnotations(field, 1, this.rootLibrary);

            CtBehavior[] behaviors = clientClass.getDeclaredBehaviors();
            for (CtBehavior behavior : behaviors) findAnnotations(behavior, 1, this.rootLibrary);
        });
    }

    @Override
    public void findInheritanceOfServerLibrary(DependencyTreeNode currentLibrary) {
        // TODO: Should this do something?
    }

    @Override
    public void visitServerLibrary(DependencyTreeNode currentLibrary) {
        // Find annotations in reachable classes
        Map<Integer, ReachableClasses> reachableClassesAtDistance = currentLibrary.getReachableApiFieldClassesAtDistance();
        reachableClassesAtDistance.forEach((distance, reachableClasses) -> {
            Set<CtClass> reachableClassesSet = reachableClasses.getReachableClassesMap().keySet();
            reachableClassesSet.forEach(reachableClass -> {
                findAnnotations(reachableClass, distance + 1, currentLibrary);

                CtField[] fields = reachableClass.getDeclaredFields();
                for (CtField field : fields) findAnnotations(field, distance + 1, this.rootLibrary);
            });
        });

        // Find annotations in reachable methods
        Map<Integer, ReachableBehaviors> reachableBehaviorsAtDistance = currentLibrary.getReachableApiBehaviorsAtDistance();
        reachableBehaviorsAtDistance.forEach((distance, reachableBehaviors) -> {
            Set<CtBehavior> reachableBehaviorsSet = reachableBehaviors.getReachableBehaviorsMap().keySet();
            reachableBehaviorsSet.forEach(reachableBehavior -> findAnnotations(reachableBehavior, distance + 1, currentLibrary));
        });
    }

    // PRIVATE METHODS

    // Shared in visitClientLibrary and visitServerLibrary
    private void findAnnotations(Object object, Integer distance, DependencyTreeNode currentLibrary) {
        try {
            Object[] annotations = {};
            if (object instanceof CtClass) {
                annotations = ((CtClass) object).getAvailableAnnotations();
            } else if (object instanceof CtBehavior) {
                annotations = ((CtBehavior) object).getAvailableAnnotations();
            } else if (object instanceof CtField) {
                annotations = ((CtField) object).getAvailableAnnotations();
            }
            computeFoundAnnotations(annotations, distance, currentLibrary);

            if (object instanceof CtBehavior) {
                Object[][] parametersAnnotations = ((CtBehavior) object).getAvailableParameterAnnotations();
                for (Object[] parameterAnnotations : parametersAnnotations)
                    computeFoundAnnotations(parameterAnnotations, distance, currentLibrary);
            }
        } catch (NoClassDefFoundError e) {
            LOG.info("No class definition: {}", e.getMessage());
        }
    }

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
        Library serverLibrary = Library.fromClassPath(annotationClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableAnnotationClass(distance, annotationClass, numUsages);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }
}
