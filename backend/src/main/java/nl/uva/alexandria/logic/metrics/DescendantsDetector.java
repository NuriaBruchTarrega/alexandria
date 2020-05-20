package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.ServerClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class DescendantsDetector {

    static int numDescendants(ServerClass sc, ClassPoolManager cpm) throws NotFoundException {
        String libraryJarPath = sc.getLibrary().getLibraryPath();
        Set<CtClass> libraryClasses = cpm.getLibraryClasses(libraryJarPath);
        List<CtClass> descendants = findDescendants(sc, libraryClasses);

        return descendants.size();
    }

    private static List<CtClass> findDescendants(ServerClass sc, Set<CtClass> libraryClasses) {
        List<CtClass> descendants = new ArrayList<>();

        CtClass serverClass = sc.getCtClass();

        for (CtClass libraryClass : libraryClasses) {

            if (!libraryClass.subclassOf(serverClass)) continue;
            descendants.add(libraryClass);
        }

        return descendants;
    }
}
