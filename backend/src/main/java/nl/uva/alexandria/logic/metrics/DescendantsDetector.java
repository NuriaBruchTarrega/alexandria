package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.ReachableFields;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DescendantsDetector {

    public static void calculateDescendantsOfDependency(DependencyTreeNode currentLibrary, ClassPoolManager classPoolManager) throws NotFoundException {
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(currentLibrary.getLibrary().getLibraryPath());
        Map<Integer, ReachableFields> reachableFieldsAtDistance = currentLibrary.getReachableFieldsAtDistance();

        for (CtClass libraryClass : libraryClasses) {
            reachableFieldsAtDistance.forEach((distance, reachability) -> {
                Map<CtClass, Integer> descendants = new HashMap<>();
                reachability.getReachableFields().forEach((reachableField, numDeclarations) -> {
                    if (!libraryClass.subclassOf(reachableField)) return;
                    if (libraryClass.equals(reachableField)) return;
                    descendants.put(libraryClass, numDeclarations);
                });
                reachability.addMultipleReachableFields(descendants);
            });
        }
    }
}
