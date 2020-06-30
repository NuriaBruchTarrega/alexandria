package nl.uva.alexandria.model.sattose;

import java.util.List;
import java.util.Map;

public class ClassData {
    private Map<Long, String> methods;
    private List<String> superInterfaces;
    private String sourceFile;
    private List<String> superClasses;

    public ClassData(Map<Long, String> methods, List<String> superInterfaces, String sourceFile, List<String> superClasses) {
        this.methods = methods;
        this.superInterfaces = superInterfaces;
        this.sourceFile = sourceFile;
        this.superClasses = superClasses;
    }

    public Map<Long, String> getMethods() {
        return methods;
    }

    public List<String> getSuperInterfaces() {
        return superInterfaces;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }
}
