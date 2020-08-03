package nl.uva.alexandria.logic.metrics;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.expr.Expr;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.DependencyTreeResult;

import java.util.Map;
import java.util.Set;

public class Aggregator {

    private Aggregator() {
    }

    public static DependencyTreeResult calculateResultTree(DependencyTreeNode dependencyTree) {
        return createResultTree(dependencyTree);
    }

    private static DependencyTreeResult createResultTree(DependencyTreeNode dependencyTree) {
        DependencyTreeResult dependencyTreeResult = new DependencyTreeResult(dependencyTree.getLibrary());

        aggregateMIC(dependencyTree, dependencyTreeResult);
        aggregateAC(dependencyTree, dependencyTreeResult);

        dependencyTree.getChildren().forEach(child -> dependencyTreeResult.addChildren(createResultTree(child)));

        return dependencyTreeResult;
    }

    private static void aggregateMIC(DependencyTreeNode dependencyTree, DependencyTreeResult dependencyTreeResult) {
        dependencyTree.getReachableMethodsAtDistance().forEach((distance, reachability) -> {
            Map<CtBehavior, Set<Expr>> reachableMethods = reachability.getReachableMethodsMap();

            // Calculate metric
            Integer result = reachableMethods.values().stream().map(Set::size).reduce(0, Integer::sum);
            dependencyTreeResult.addMicAtDistance(distance, result);

            // Calculate distribution per class
            reachableMethods.values().forEach(methodCalls -> methodCalls.forEach(methodCall -> dependencyTreeResult.addMicConnectionFromClass(methodCall.where().getDeclaringClass().getName())));
        });
    }

    private static void aggregateAC(DependencyTreeNode dependencyTree, DependencyTreeResult dependencyTreeResult) {
        dependencyTree.getReachableFieldsAtDistance().forEach((distance, reachability) -> {
            Map<CtClass, Set<CtField>> reachableFields = reachability.getReachableFieldsMap();

            // Calculate metric
            Integer result = reachableFields.values().stream().map(Set::size).reduce(0, Integer::sum);
            dependencyTreeResult.addAcAtDistance(distance, result);

            // Calculate distribution per class
            reachableFields.values().forEach(ctFields -> ctFields.forEach(ctField -> dependencyTreeResult.addAcConnectionFromClass(ctField.getDeclaringClass().getName())));
        });
    }
}
