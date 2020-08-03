package nl.uva.alexandria.model;

import javassist.CtClass;
import javassist.CtField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReachableClasses {

    private Map<CtClass, Set<CtField>> reachableClassesMap = new HashMap<>();

    public Map<CtClass, Set<CtField>> getReachableClassesMap() {
        return reachableClassesMap;
    }

    public void addReachableClass(CtClass ctClass, Set<CtField> declarations) {
        this.reachableClassesMap.computeIfPresent(ctClass, (key, value) -> {
            Set<CtField> notIncluded = declarations.stream().filter(ctField -> !value.contains(ctField)).collect(Collectors.toSet());
            if (notIncluded.isEmpty()) value.addAll(notIncluded);
            return value;
        });
        this.reachableClassesMap.putIfAbsent(ctClass, declarations);
    }

    public void addMultipleReachableClasses(Map<CtClass, Set<CtField>> newReachableFields) {
        newReachableFields.forEach(this::addReachableClass);
    }
}
