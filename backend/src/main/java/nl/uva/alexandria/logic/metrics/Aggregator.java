package nl.uva.alexandria.logic.metrics;

import javassist.CtBehavior;
import javassist.CtClass;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.Map;

public class Aggregator {

    public static DependencyTreeResult calculateResultTree(DependencyTreeNode dependencyTree) {
        return createResultTree(dependencyTree);
    }

    private static DependencyTreeResult createResultTree(DependencyTreeNode dependencyTree) {
        DependencyTreeResult dependencyTreeResult = new DependencyTreeResult(dependencyTree.getLibrary());

        dependencyTree.getReachableMethodsAtDistance().forEach((distance, reachability) -> {
            Map<CtBehavior, Integer> reachableMethods = reachability.getReachableMethods();
            Integer result = reachableMethods.values().stream().reduce(0, Integer::sum);
            dependencyTreeResult.addMicAtDistance(distance, result);
        });

        dependencyTree.getReachableFieldsAtDistance().forEach((distance, reachability) -> {
            Map<CtClass, Integer> reachableFields = reachability.getReachableFields();
            Integer result = reachableFields.values().stream().reduce(0, Integer::sum);
            dependencyTreeResult.addAcAtDistance(distance, result);
        });

        dependencyTree.getChildren().forEach(child -> {
            dependencyTreeResult.addChildren(createResultTree(child));
        });

        return dependencyTreeResult;
    }
}
