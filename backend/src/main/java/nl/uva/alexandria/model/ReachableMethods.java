package nl.uva.alexandria.model;

import javassist.CtBehavior;
import javassist.expr.MethodCall;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReachableMethods {

    private Map<CtBehavior, Set<MethodCall>> reachableMethods = new HashMap<>();

    public Map<CtBehavior, Set<MethodCall>> getReachableMethods() {
        return reachableMethods;
    }

    public void addReachableMethod(CtBehavior ctBehavior, Set<MethodCall> reachableFrom) {
        this.reachableMethods.computeIfPresent(ctBehavior, (key, value) -> {
            Set<MethodCall> notIncluded = reachableFrom.stream().filter(methodCall -> !value.contains(methodCall)).collect(Collectors.toSet());
            if (notIncluded.size() != 0) value.addAll(notIncluded);
            return value;
        });
        this.reachableMethods.putIfAbsent(ctBehavior, reachableFrom);
    }

    public void addMultipleReachableMethods(Map<CtBehavior, Set<MethodCall>> newReachableMethods) {
        newReachableMethods.forEach(this::addReachableMethod);
    }
}
