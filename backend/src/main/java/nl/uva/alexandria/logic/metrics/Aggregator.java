package nl.uva.alexandria.logic.metrics;

import javassist.CtBehavior;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;

import java.util.HashMap;
import java.util.List;
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

    public static Map<Library, Integer> obtainDirectCouplingCalculation(DependencyTreeNode root) {
        Map<Library, Integer> directCouplings = new HashMap<>();
        List<DependencyTreeNode> directDependencies = root.getChildren();

        directDependencies.forEach(directDependency -> {
            Map<CtBehavior, Integer> individualCallsPerMethod = directDependency.getReachableApiBehaviorsWithNumCalls();
            Integer numIndividualCalls = individualCallsPerMethod.values().stream().reduce(0, Integer::sum);
            directCouplings.put(directDependency.getLibrary(), numIndividualCalls);
        });

        return directCouplings;
    }
}
