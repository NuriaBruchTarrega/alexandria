package nl.uva.alexandria.model;

import javassist.CtClass;
import javassist.CtField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReachableFields {

    private Map<CtClass, Set<CtField>> reachableFieldsMap = new HashMap<>();

    public Map<CtClass, Set<CtField>> getReachableFieldsMap() {
        return reachableFieldsMap;
    }

    public void addReachableClass(CtClass ctClass, Set<CtField> declarations) {
        this.reachableFieldsMap.computeIfPresent(ctClass, (key, value) -> {
            Set<CtField> notIncluded = declarations.stream().filter(ctField -> !value.contains(ctField)).collect(Collectors.toSet());
            if (notIncluded.isEmpty()) value.addAll(notIncluded);
            return value;
        });
        this.reachableFieldsMap.putIfAbsent(ctClass, declarations);
    }

    public void addMultipleReachableClasses(Map<CtClass, Set<CtField>> newReachableFields) {
        newReachableFields.forEach(this::addReachableClass);
    }
}
