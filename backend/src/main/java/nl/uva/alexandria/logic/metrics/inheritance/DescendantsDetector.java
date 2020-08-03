package nl.uva.alexandria.logic.metrics.inheritance;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.ReachableClasses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DescendantsDetector extends InheritanceDetector {

    public DescendantsDetector(ClassPoolManager classPoolManager) {
        super(classPoolManager);
    }

    @Override
    public void calculateInheritanceOfDependencyTreeNode(DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        updateCurrentLibrary(dependencyTreeNode.getLibrary());
        Map<Integer, ReachableClasses> reachableFieldsAtDistance = dependencyTreeNode.getReachableClassesAtDistance();

        for (CtClass libraryClass : this.currentLibraryClasses) {
            for (Map.Entry<Integer, ReachableClasses> entry : reachableFieldsAtDistance.entrySet()) {
                findDescendantsOfReachableClasses(libraryClass, entry.getValue());
            }
        }
    }

    public Set<CtClass> findDescendantsOfClass(CtClass ctClass, DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        this.updateCurrentLibrary(dependencyTreeNode.getLibrary());
        Set<CtClass> descendants = new HashSet<>();

        for (CtClass libraryClass : this.currentLibraryClasses) {
            if (libraryClassImplementsOrExtendsReachableClass(libraryClass, ctClass)) {
                descendants.add(libraryClass);
            }
        }

        return descendants;
    }

    private void findDescendantsOfReachableClasses(CtClass libraryClass, ReachableClasses reachability) throws NotFoundException {
        Map<CtClass, Set<CtField>> descendants = new HashMap<>();
        for (Map.Entry<CtClass, Set<CtField>> entry : reachability.getReachableClassesMap().entrySet()) {
            CtClass reachableClass = entry.getKey();
            Set<CtField> declarations = entry.getValue();
            if (libraryClassImplementsOrExtendsReachableClass(libraryClass, reachableClass)) {
                descendants.put(libraryClass, declarations);
            }
        }
        reachability.addMultipleReachableClasses(descendants);
    }
}
