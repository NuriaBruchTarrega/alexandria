package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AnnotationsCalculator {

    protected static final Logger LOG = LoggerFactory.getLogger(AnnotationsCalculator.class);
    private final ClassPoolManager classPoolManager;
    private final DependencyTreeNode rootLibrary;

    public AnnotationsCalculator(ClassPoolManager classPoolManager, DependencyTreeNode rootLibrary) {
        this.classPoolManager = classPoolManager;
        this.rootLibrary = rootLibrary;
    }

    public Set<CtClass> findAnnotations(Object object, Integer distance, DependencyTreeNode currentLibrary) {
        Set<CtClass> annotationsInLibrary = new HashSet<>();

        try {
            Object[] annotations = {};
            if (object instanceof CtClass) {
                annotations = ((CtClass) object).getAvailableAnnotations();
            } else if (object instanceof CtBehavior) {
                annotations = ((CtBehavior) object).getAvailableAnnotations();
            } else if (object instanceof CtField) {
                annotations = ((CtField) object).getAvailableAnnotations();
            }
            Set<CtClass> annotationsInObject = computeFoundAnnotations(annotations, distance, currentLibrary);
            annotationsInLibrary.addAll(annotationsInObject);

            if (object instanceof CtBehavior) {
                Object[][] parametersAnnotations = ((CtBehavior) object).getAvailableParameterAnnotations();
                for (Object[] parameterAnnotations : parametersAnnotations) {
                    Set<CtClass> annotationsInParameters = computeFoundAnnotations(parameterAnnotations, distance, currentLibrary);
                    annotationsInLibrary.addAll(annotationsInParameters);
                }
            }
        } catch (NoClassDefFoundError e) {
            LOG.info("No class definition: {}", e.getMessage());
        }

        return annotationsInLibrary;
    }

    private Set<CtClass> computeFoundAnnotations(Object[] annotations, Integer distance, DependencyTreeNode currentLibrary) {
        Set<CtClass> annotationsInLibrary = new HashSet<>();
        if (annotations.length == 0) return annotationsInLibrary;

        for (Object annotation : annotations) {
            String annotationName = ((Annotation) annotation).annotationType().getName();
            try {
                CtClass annotationClass = classPoolManager.getClassFromClassName(annotationName);
                if (classPoolManager.isStandardClass(annotationClass)) return annotationsInLibrary;
                if (classPoolManager.isClassInDependency(annotationClass, currentLibrary.getLibrary().getLibraryPath())) {
                    addReachableAnnotation(annotationClass, distance + 1, 1);
                } else if (!currentLibrary.equals(this.rootLibrary)) {
                    currentLibrary.addReachableAnnotationClass(distance + 1, annotationClass, 1);
                    annotationsInLibrary.add(annotationClass);
                }
            } catch (NotFoundException e) {
                LOG.info("Annotation class not found: {}", e.getMessage());
            }
        }

        return annotationsInLibrary;
    }

    private void addReachableAnnotation(CtClass annotationClass, Integer distance, Integer numUsages) throws NotFoundException {
        CtBehavior[] behaviors = annotationClass.getDeclaredBehaviors();
        Library serverLibrary = Library.fromClassPath(annotationClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableAnnotationClass(distance, annotationClass, numUsages);
            for (CtBehavior behavior : behaviors) {
                libraryNode.get().addReachableBehavior(behavior, distance);
            }
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }
}
