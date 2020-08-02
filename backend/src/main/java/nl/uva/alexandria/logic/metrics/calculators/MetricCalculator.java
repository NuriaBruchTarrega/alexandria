package nl.uva.alexandria.logic.metrics.calculators;

import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.InheritanceDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MetricCalculator {
    protected static final Logger LOG = LoggerFactory.getLogger(MetricCalculator.class);

    protected final ClassPoolManager classPoolManager;
    protected final InheritanceDetector inheritanceDetector;

    public MetricCalculator(ClassPoolManager classPoolManager, InheritanceDetector inheritanceDetector) {
        this.classPoolManager = classPoolManager;
        this.inheritanceDetector = inheritanceDetector;
    }

    public abstract DependencyTreeNode calculateMetric(DependencyTreeNode dependencyTreeNode);
}
