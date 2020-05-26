package nl.uva.alexandria.logic.metrics;

import javassist.*;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.ServerMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class PolymorphismDetection {

    private PolymorphismDetection() {
    }

    static int numPolymorphicMethods(ServerMethod sm, ClassPoolManager cpm) throws NotFoundException {
        String libraryJarPath = sm.getLibrary().getLibraryPath();
        Set<CtClass> libraryClasses = cpm.getLibraryClasses(libraryJarPath);
        List<CtBehavior> polymorphicMethods = findPolymorphicMethods(sm, libraryClasses);

        return polymorphicMethods.size();
    }

    private static List<CtBehavior> findPolymorphicMethods(ServerMethod sm, Set<CtClass> libraryClasses) {
        List<CtBehavior> polymorphicMethods = new ArrayList<>();

        CtClass serverClass = sm.getCtClass();
        CtBehavior serverMethod = sm.getBehavior();

        for (CtClass libraryClass : libraryClasses) {
            if (!libraryClass.subclassOf(serverClass)) continue;
            try {
                CtBehavior polymorphicMethod;

                if (serverMethod instanceof CtMethod)
                    polymorphicMethod = libraryClass.getDeclaredMethod(serverMethod.getName(), serverMethod.getParameterTypes());
                else if (serverMethod instanceof CtConstructor)
                    polymorphicMethod = libraryClass.getConstructor(serverMethod.getSignature());
                else continue; // In case at some point there is another type of behavior

                polymorphicMethods.add(polymorphicMethod);
            } catch (NotFoundException e) {
                // Class does not have polymorphic implementation of the method
                continue;
            }
        }

        return polymorphicMethods;
    }
}
