package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.DescendantsDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.factories.LibraryFactory;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

public class AnnotationsCalculator extends MetricCalculator {

    public AnnotationsCalculator(ClassPoolManager classPoolManager) {
        super(classPoolManager, new DescendantsDetector(classPoolManager));
    }

    @Override
    public DependencyTreeNode calculateMetric(DependencyTreeNode dependencyTreeNode) {
        this.rootLibrary = dependencyTreeNode;
        calculateDirectCoupling();
        return dependencyTreeNode;
    }

    private void calculateDirectCoupling() {
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();

        clientClasses.forEach(clientClass -> {
            //TODO: catch annotations in fields and in variables
            try {
                Object[] annotations = clientClass.getAvailableAnnotations();
                computeFoundAnnotations(annotations);
            } catch (NoClassDefFoundError e1) {
                LOG.info("No class definition: {}", e1.getMessage());
            }

            CtBehavior[] behaviors = clientClass.getDeclaredBehaviors();
            for (CtBehavior behavior : behaviors) {
                try {
                    Object[] annotations = behavior.getAvailableAnnotations();
                    computeFoundAnnotations(annotations);
                } catch (NoClassDefFoundError e1) {
                    LOG.info("No class definition: {}", e1.getMessage());
                }
            }
        });
    }

    private void computeFoundAnnotations(Object[] annotations) {
        if (annotations.length == 0) return;

        for (Object annotation : annotations) {
            String annotationName = ((Annotation) annotation).annotationType().getName();
            try {
                CtClass annotationClass = classPoolManager.getClassFromClassName(annotationName);
                if (classPoolManager.isClassInDependency(annotationClass)) {
                    addReachableAnnotation(annotationClass, 1, 1);
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
