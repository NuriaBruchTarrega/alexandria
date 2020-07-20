package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.ReachableFields;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DescendantsDetector {

    static void calculateDescendantsOfDependency(DependencyTreeNode currentLibrary, ClassPoolManager classPoolManager) throws NotFoundException {
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(currentLibrary.getLibrary().getLibraryPath());
        Map<Integer, ReachableFields> reachableFieldsAtDistance = currentLibrary.getReachableFieldsAtDistance();

        for (CtClass libraryClass : libraryClasses) {
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
}
