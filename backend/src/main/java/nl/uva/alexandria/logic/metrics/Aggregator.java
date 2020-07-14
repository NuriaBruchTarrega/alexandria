package nl.uva.alexandria.logic.metrics;

import javassist.CtBehavior;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;

import java.util.Map;

public class Aggregator {

    public static Map<Library, Integer> joinByLibrary(Map<? extends ServerClass, Integer> mapToJoin, Map<Library, Integer> joinedByLibrary) {
        mapToJoin.forEach((serverClass, num) -> {
            Library library = serverClass.getLibrary();
            joinedByLibrary.computeIfPresent(library, (key, value) -> value + num);
            joinedByLibrary.putIfAbsent(library, num);
        });

        return joinedByLibrary;
    }

    public static DependencyTreeResult calculateMethodInvocationCoupling(DependencyTreeNode dependencyTree) {
        return createResultTree(dependencyTree);
    }

    private static DependencyTreeResult createResultTree(DependencyTreeNode dependencyTree) {
        DependencyTreeResult dependencyTreeResult = new DependencyTreeResult(dependencyTree.getLibrary());

        dependencyTree.getReachableMethodsAtDistance().forEach((distance, reachability) -> {
            Map<CtBehavior, Integer> reachableMethods = reachability.getReachableMethods();
            Integer result = reachableMethods.values().stream().reduce(0, Integer::sum);
            dependencyTreeResult.addMicAtDistance(distance, result);
        });

        dependencyTree.getChildren().forEach(child -> {
            dependencyTreeResult.addChildren(createResultTree(child));
        });

        return dependencyTreeResult;
    }
}
