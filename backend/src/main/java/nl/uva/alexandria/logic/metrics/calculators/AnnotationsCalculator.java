package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtBehavior;
import javassist.CtClass;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.DescendantsDetector;
import nl.uva.alexandria.model.DependencyTreeNode;

import java.lang.annotation.Annotation;
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
            System.out.println(((Annotation) annotation).annotationType().getName());
        }
    }
}
