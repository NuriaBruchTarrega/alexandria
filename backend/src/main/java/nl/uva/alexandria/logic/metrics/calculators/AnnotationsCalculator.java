package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtClass;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.InheritanceDetector;
import nl.uva.alexandria.model.DependencyTreeNode;

import java.util.Set;

public class AnnotationsCalculator extends MetricCalculator {

    public AnnotationsCalculator(ClassPoolManager classPoolManager, InheritanceDetector inheritanceDetector) {
        super(classPoolManager, inheritanceDetector);
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
                Object[] annotations = clientClass.getAnnotations();
            } catch (ClassNotFoundException e) {
                LOG.info("Not found annotation: {}", e.getMessage());
            }
        });
    }
}
