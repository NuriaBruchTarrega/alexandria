package nl.uva.alexandria.logic.metrics;

import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.calculators.AggregationCalculator;
import nl.uva.alexandria.logic.metrics.calculators.MethodInvocationsCalculator;
import nl.uva.alexandria.logic.metrics.calculators.MetricCalculator;
import nl.uva.alexandria.model.DependencyTreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class DependencyTreeTraverser {

    private final ClassPoolManager classPoolManager;
    private MetricCalculator methodCalculator;
    private MetricCalculator classCalculator;

    public DependencyTreeTraverser(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public void traverseTree(DependencyTreeNode rootNode) {
        methodCalculator = new MethodInvocationsCalculator(classPoolManager, rootNode);
        classCalculator = new AggregationCalculator(classPoolManager, rootNode);
        traverseClientLibrary();
        iterateTree(rootNode);
    }

    private void traverseClientLibrary() {
        // Find method calls to other libraries
        // Find params from other libraries
        // Find return types from other libraries
        // Find annotations in methods and parameters
        methodCalculator.visitClientLibrary();
        // Find fields from other libraries
        // Find superclasses in other libraries
        // Find annotations in classes and fields
        classCalculator.visitClientLibrary();
    }

    private void iterateTree(DependencyTreeNode rootNode) {
        Queue<DependencyTreeNode> toVisit = new ArrayDeque<>(rootNode.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            // 1. Check for reachability at all
            // 2. Find inheritance of the reachable stuff
            traverseServerLibrary(visiting);
            toVisit.addAll(visiting.getChildren());
        }
    }

    private void traverseServerLibrary(DependencyTreeNode currentLibrary) {
        // Treat inheritance
        methodCalculator.findInheritanceOfServerLibrary(currentLibrary);
        classCalculator.findInheritanceOfServerLibrary(currentLibrary);

        // 1. Reachable API methods
        // Follow the trace through method calls - special map
        // Find params and return types and either add to toVisit or add to reachable of other libraries
        methodCalculator.visitServerLibrary(currentLibrary);

        // 2. Reachable API classes
        // Follow the trace through field declaration - special map
        // Find superclasses and either add to toVisit or add to reachable of other libraries
        classCalculator.visitServerLibrary(currentLibrary);
    }
}
