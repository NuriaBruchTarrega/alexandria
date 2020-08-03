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
        dependencyTree.getReachableBehaviorsAtDistance().forEach((distance, reachability) -> {
            Map<CtBehavior, Set<Expr>> reachableBehaviors = reachability.getReachableBehaviorsMap();

            // Calculate metric
            Integer result = reachableBehaviors.values().stream().map(Set::size).reduce(0, Integer::sum);
            dependencyTreeResult.addMicAtDistance(distance, result);

            // Calculate distribution per class
            reachableBehaviors.values().forEach(methodCalls -> methodCalls.forEach(methodCall -> dependencyTreeResult.addMicConnectionFromClass(methodCall.where().getDeclaringClass().getName())));
        });
    }

    private static void aggregateAC(DependencyTreeNode dependencyTree, DependencyTreeResult dependencyTreeResult) {
        dependencyTree.getReachableClassesAtDistance().forEach((distance, reachability) -> {
            Map<CtClass, Set<CtField>> reachableClassesMap = reachability.getReachableClassesMap();

            // Calculate metric
            Integer result = reachableClassesMap.values().stream().map(Set::size).reduce(0, Integer::sum);
            dependencyTreeResult.addAcAtDistance(distance, result);

            // Calculate distribution per class
            reachableClassesMap.values().forEach(ctFields -> ctFields.forEach(ctField -> dependencyTreeResult.addAcConnectionFromClass(ctField.getDeclaringClass().getName())));
        });
    }
}
