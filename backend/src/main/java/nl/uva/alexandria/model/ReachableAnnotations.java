package nl.uva.alexandria.model;

import javassist.CtClass;

import java.util.HashMap;
import java.util.Map;

public class ReachableAnnotations {
    private Map<CtClass, Integer> reachableAnnotationsMap = new HashMap<>();

    public Map<CtClass, Integer> getReachableAnnotationsMap() {
        return reachableAnnotationsMap;
    }

    public void addReachableAnnotation(CtClass annotationClass, Integer num) {
        this.reachableAnnotationsMap.computeIfPresent(annotationClass, (key, value) -> value + num);
        this.reachableAnnotationsMap.putIfAbsent(annotationClass, num);
    }
}
