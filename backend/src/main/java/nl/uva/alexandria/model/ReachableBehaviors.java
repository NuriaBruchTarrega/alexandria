package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.expr.Expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReachableBehaviors {

    private Map<CtBehavior, Set<Expr>> reachableBehaviorsMap = new HashMap<>();

    public Map<CtBehavior, Set<Expr>> getReachableBehaviorsMap() {
        return reachableBehaviorsMap;
    }

    public void addReachableMethod(CtBehavior ctBehavior, Set<Expr> reachableFrom) {
        this.reachableBehaviorsMap.computeIfPresent(ctBehavior, (key, value) -> {
            Set<Expr> notIncluded = reachableFrom.stream().filter(methodCall -> !value.contains(methodCall)).collect(Collectors.toSet());
            if (notIncluded.isEmpty()) value.addAll(notIncluded);
            return value;
        });
        this.reachableBehaviorsMap.putIfAbsent(ctBehavior, reachableFrom);
    }

    public void addMultipleReachableMethods(Map<CtBehavior, Set<Expr>> newReachableBehaviors) {
        newReachableBehaviors.forEach(this::addReachableMethod);
    }
}
