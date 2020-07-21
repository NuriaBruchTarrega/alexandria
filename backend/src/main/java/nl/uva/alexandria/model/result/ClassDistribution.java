package nl.uva.alexandria.model.result;

import java.util.HashMap;
import java.util.Map;

public class ClassDistribution {
    Map<String, Integer> classDistribution;

    public ClassDistribution() {
        this.classDistribution = new HashMap<>();
    }

    public void addMethodCallFromClass(String className) {
        classDistribution.computeIfPresent(className, (key, value) -> value + 1);
        classDistribution.putIfAbsent(className, 1);
    }

    public Map<String, Integer> getClassDistribution() {
        return classDistribution;
    }

    public void setClassDistribution(Map<String, Integer> classDistribution) {
        this.classDistribution = classDistribution;
    }
}
