package nl.uva.alexandria.model;

import javassist.CtClass;

import java.util.HashMap;
import java.util.Map;

public class ReachableFields {

    private Map<CtClass, Integer> reachableFields = new HashMap<>();

    public Map<CtClass, Integer> getReachableFields() {
        return reachableFields;
    }

    public void addReachableField(CtClass ctField, Integer numDeclarationsInClient) {
        this.reachableFields.computeIfPresent(ctField, (key, value) -> value + numDeclarationsInClient);
        this.reachableFields.putIfAbsent(ctField, numDeclarationsInClient);
    }

    public void addMultipleReachableFields(Map<CtClass, Integer> newReachableFields) {
        newReachableFields.forEach(this::addReachableField);
    }
}
