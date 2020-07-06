package nl.uva.alexandria.logic.metrics;

import javassist.CtBehavior;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableMethods;
import nl.uva.alexandria.model.ServerClass;

import java.util.*;

public class Aggregator {

    public static Map<Library, Integer> joinByLibrary(Map<? extends ServerClass, Integer> mapToJoin, Map<Library, Integer> joinedByLibrary) {
        mapToJoin.forEach((serverClass, num) -> {
            Library library = serverClass.getLibrary();
            joinedByLibrary.computeIfPresent(library, (key, value) -> value + num);
            joinedByLibrary.putIfAbsent(library, num);
        });

        return joinedByLibrary;
    }

    public static Map<Library, Integer> obtainDirectCouplingCalculation(DependencyTreeNode root) {
        Map<Library, Integer> directCouplings = new HashMap<>();
        List<DependencyTreeNode> directDependencies = root.getChildren();

        directDependencies.forEach(directDependency -> {
            Map<CtBehavior, Integer> individualCallsPerMethod = directDependency.getReachableApiBehaviorsWithNumCallsAtDistance(1);
            Integer numIndividualCalls = individualCallsPerMethod.values().stream().reduce(0, Integer::sum);
            directCouplings.put(directDependency.getLibrary(), numIndividualCalls);
        });

        return directCouplings;
    }

    public static Map<Library, Integer> calculateMethodInvocationCoupling(DependencyTreeNode dependencyTree, Map<Library, Integer> mapAllDependencies) {
        Queue<DependencyTreeNode> toVisit = new LinkedList<>(dependencyTree.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();

            Integer mic = calculateMic(visiting);
            mapAllDependencies.computeIfPresent(visiting.getLibrary(), (key, value) -> value + mic);
            mapAllDependencies.putIfAbsent(visiting.getLibrary(), mic);

            toVisit.addAll(visiting.getChildren());
        }

        return mapAllDependencies;
    }

    private static Integer calculateMic(DependencyTreeNode dependencyTreeNode) {
        Map<Integer, ReachableMethods> reachableMethodsDistanceMap = dependencyTreeNode.getReachableMethodsAtDistance();

        Integer mic = reachableMethodsDistanceMap.entrySet().stream().map(entry -> {
            Integer distance = entry.getKey();
            ReachableMethods reachability = entry.getValue();

            Integer result = reachability.getReachableMethods().values().stream().reduce(0, Integer::sum);
            return result / distance;
        }).reduce(0, Integer::sum);

        return mic;
    }
}
