package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.ServerMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PolymorphismDetection {

    public static int numPolymorphicMethods(ServerMethod sm, ClassPoolManager cpm) throws NotFoundException {
        String libraryJarPath = sm.getLibrary().getLibraryPath();
        Set<CtClass> libraryClasses = cpm.getLibraryClasses(libraryJarPath);
        List<CtMethod> polymorphicMethods = findPolymorphicMethods(sm, libraryClasses);

        return polymorphicMethods.size();
    }

    private static List<CtMethod> findPolymorphicMethods(ServerMethod sm, Set<CtClass> libraryClasses) {
        List<CtMethod> polymorphicMethods = new ArrayList<>();

        CtClass serverClass = sm.getCtClass();
        CtMethod serverMethod = sm.getMethod();

        for (CtClass libraryClass : libraryClasses) {

            if (!libraryClass.subclassOf(serverClass)) continue;
            try {
                CtMethod polymorphicMethod = libraryClass.getDeclaredMethod(serverMethod.getName(), serverMethod.getParameterTypes());
                polymorphicMethods.add(polymorphicMethod);
            } catch (NotFoundException e) {
                continue;
            }
        }

        return polymorphicMethods;
    }
}
