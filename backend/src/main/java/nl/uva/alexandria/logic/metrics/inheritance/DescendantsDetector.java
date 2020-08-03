package nl.uva.alexandria.logic.metrics.inheritance;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.ReachableFields;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DescendantsDetector extends InheritanceDetector {

    public DescendantsDetector(ClassPoolManager classPoolManager) {
        super(classPoolManager);
    }

    public void calculateInheritanceOfDependencyTreeNode(DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        updateCurrentLibrary(dependencyTreeNode.getLibrary());
        Map<Integer, ReachableFields> reachableFieldsAtDistance = dependencyTreeNode.getReachableFieldsAtDistance();

        for (CtClass libraryClass : this.currentLibraryClasses) {
            reachableFieldsAtDistance.forEach((distance, reachability) -> {
                Map<CtClass, Set<CtField>> descendants = new HashMap<>();
                reachability.getReachableFieldsMap().forEach((reachableField, declarations) -> {
                    if (isClassDescendant(reachableField, libraryClass)) {
                        descendants.put(libraryClass, declarations);
                    }
                });
                reachability.addMultipleReachableClasses(descendants);
            });
        }
    }

    public Set<CtClass> findDescendantsOfClass(CtClass ctClass, DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        this.updateCurrentLibrary(dependencyTreeNode.getLibrary());
        Set<CtClass> descendants = new HashSet<>();

        for (CtClass libraryClass : this.currentLibraryClasses) {
            if (isClassDescendant(ctClass, libraryClass)) {
                descendants.add(libraryClass);
            }
        }

        return descendants;
    }

    boolean isClassDescendant(CtClass clazz, CtClass possibleDescendant) {
        if (!possibleDescendant.subclassOf(clazz)) return false;
        return !possibleDescendant.equals(clazz);
    }
}
