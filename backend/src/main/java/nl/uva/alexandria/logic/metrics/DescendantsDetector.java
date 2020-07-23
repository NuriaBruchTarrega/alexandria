package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableFields;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DescendantsDetector {
    ClassPoolManager classPoolManager;
    Set<CtClass> currentLibraryClasses;
    Library currentLibrary = null;

    public DescendantsDetector(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    void calculateDescendantsOfDependency(DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        updateCurrentLibrary(dependencyTreeNode.getLibrary());
        Map<Integer, ReachableFields> reachableFieldsAtDistance = dependencyTreeNode.getReachableFieldsAtDistance();

        for (CtClass libraryClass : this.currentLibraryClasses) {
            reachableFieldsAtDistance.forEach((distance, reachability) -> {
                Map<CtClass, Set<CtField>> descendants = new HashMap<>();
                reachability.getReachableFields().forEach((reachableField, declarations) -> {
                    if (!libraryClass.subclassOf(reachableField)) return;
                    if (libraryClass.equals(reachableField)) return;
                    descendants.put(libraryClass, declarations);
                });
                reachability.addMultipleReachableClasses(descendants);
            });
        }
    }

    void updateCurrentLibrary(Library library) throws NotFoundException {
        if (!library.equals(currentLibrary)) {
            this.currentLibrary = library;
            this.currentLibraryClasses = classPoolManager.getLibraryClasses(library.getLibraryPath());
        }
    }
}
