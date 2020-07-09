package nl.uva.alexandria.model;

import javassist.CtBehavior;

import java.util.HashMap;
import java.util.Map;

public class ReachableMethods {

    private Map<CtBehavior, Integer> reachableMethods = new HashMap<>();
    ;

    public Map<CtBehavior, Integer> getReachableMethods() {
        return reachableMethods;
    }

    public void addReachableMethod(CtBehavior ctBehavior, Integer numCallsInClient) {
        this.reachableMethods.computeIfPresent(ctBehavior, (key, value) -> value + numCallsInClient);
        this.reachableMethods.putIfAbsent(ctBehavior, numCallsInClient);
    }

    public void addMultipleReachableMethods(Map<CtBehavior, Integer> newReachableMethods) {
        newReachableMethods.forEach(this::addReachableMethod);
    }
}