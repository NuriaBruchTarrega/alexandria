package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.expr.Expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReachableMethods {

    private Map<CtBehavior, Set<Expr>> reachableMethods = new HashMap<>();

    public Map<CtBehavior, Set<Expr>> getReachableMethods() {
        return reachableMethods;
    }

    public void addReachableMethod(CtBehavior ctBehavior, Set<Expr> reachableFrom) {
        this.reachableMethods.computeIfPresent(ctBehavior, (key, value) -> {
            Set<Expr> notIncluded = reachableFrom.stream().filter(methodCall -> !value.contains(methodCall)).collect(Collectors.toSet());
            if (notIncluded.size() != 0) value.addAll(notIncluded);
            return value;
        });
        this.reachableMethods.putIfAbsent(ctBehavior, reachableFrom);
    }

    public void addMultipleReachableMethods(Map<CtBehavior, Set<Expr>> newReachableMethods) {
        newReachableMethods.forEach(this::addReachableMethod);
    }
}
