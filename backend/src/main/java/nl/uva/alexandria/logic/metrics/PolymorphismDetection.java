package nl.uva.alexandria.logic.metrics;

import javassist.*;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

class PolymorphismDetection {

    private static final Logger LOG = LoggerFactory.getLogger(PolymorphismDetection.class);

    public static Map<ServerMethod, Integer> countPolymorphism(Map<ServerMethod, Integer> stableInvokedMethods, ClassPoolManager classPoolManager) {
        Set<ServerMethod> serverMethods = stableInvokedMethods.keySet();
        Map<ServerMethod, Integer> mapPolymorphicImplementations = new HashMap<>();
        Map<Library, List<ServerMethod>> mapLibraryServerMethod = serverMethods.stream().collect(Collectors.groupingBy(m -> m.getLibrary()));

        mapLibraryServerMethod.forEach(((library, serverMethodList) -> {
            try {
                Map<ServerMethod, Integer> mapLibraryPolymorphicImplementations = calculatePolymorphism(library, serverMethodList, classPoolManager);
                mapPolymorphicImplementations.putAll(mapLibraryPolymorphicImplementations);
            } catch (NotFoundException e) {
                LOG.error("Library classes not found: {}", stackTraceToString(e));
            }
        }));

        serverMethods.forEach(serverMethod -> {
            Integer numPolymorphicImplementations = mapPolymorphicImplementations.get(serverMethod);
            stableInvokedMethods.compute(serverMethod, (key, value) -> value * numPolymorphicImplementations);
        });

        return stableInvokedMethods;
    }

    private static Map<ServerMethod, Integer> calculatePolymorphism(Library library, List<ServerMethod> serverMethodList, ClassPoolManager classPoolManager) throws NotFoundException {
        Map<ServerMethod, Integer> mapPolymorphicImplementations = serverMethodList.stream().collect(Collectors.toMap(serverMethod -> serverMethod, serverMethod -> 0));
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(library.getLibraryPath());

        for (CtClass libraryClass : libraryClasses) {
            for (ServerMethod serverMethod : serverMethodList) {

                if (!libraryClass.subclassOf(serverMethod.getCtClass())) continue;
                try {
                    CtBehavior invokedMethod = serverMethod.getBehavior();

                    if (invokedMethod instanceof CtMethod) {
                        // If there is no exception, the method has been found
                        libraryClass.getDeclaredMethod(invokedMethod.getName(), invokedMethod.getParameterTypes());
                    } else if (invokedMethod instanceof CtConstructor) {
                        // If there is no exception, the constructor has been found
                        libraryClass.getConstructor(invokedMethod.getSignature());
                    } else continue; // In case at some point there is another type of behavior

                    mapPolymorphicImplementations.computeIfPresent(serverMethod, (key, value) -> value + 1);
                } catch (NotFoundException e) {
                    // Class does not have polymorphic implementation of the method
                    continue;
                }
            }
        }

        return mapPolymorphicImplementations;
    }
}
