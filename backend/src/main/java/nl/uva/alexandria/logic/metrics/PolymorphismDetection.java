package nl.uva.alexandria.logic.metrics;

import javassist.*;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableMethods;
import nl.uva.alexandria.model.ServerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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

    public static void calculatePolymorphismOfDependency(DependencyTreeNode dependencyTreeNode, ClassPoolManager classPoolManager) throws NotFoundException {
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(dependencyTreeNode.getLibrary().getLibraryPath());
        Map<Integer, ReachableMethods> reachableMethodsAtDistance = dependencyTreeNode.getReachableMethodsAtDistance();

        for (CtClass libraryClass : libraryClasses) {
            reachableMethodsAtDistance.forEach((distance, reachability) -> {
                Map<CtBehavior, Integer> polymorphicImplementations = new HashMap<>();
                reachability.getReachableMethods().forEach((reachableMethod, numLines) -> {
                    Optional<CtBehavior> polymorphicImplementationOpt = findPolymorphicImplementation(libraryClass, reachableMethod);
                    polymorphicImplementationOpt.ifPresent(behavior -> polymorphicImplementations.put(behavior, numLines));
                });
                reachability.addMultipleReachableMethods(polymorphicImplementations);
            });
        }
    }

    private static Optional<CtBehavior> findPolymorphicImplementation(CtClass libraryClass, CtBehavior reachableMethod) {
        if (libraryClass.equals(reachableMethod.getDeclaringClass())) return Optional.empty();
        if (!libraryClass.subclassOf(reachableMethod.getDeclaringClass())) return Optional.empty();

        try {
            CtBehavior foundBehavior = null;
            if (reachableMethod instanceof CtMethod) {
                // If there is no exception, the method has been found
                foundBehavior = libraryClass.getDeclaredMethod(reachableMethod.getName(), reachableMethod.getParameterTypes());
            } else if (reachableMethod instanceof CtConstructor) {
                // If there is no exception, the constructor has been found
                foundBehavior = libraryClass.getConstructor(reachableMethod.getSignature());
            } else return Optional.empty(); // In case at some point there is another type of behavior

            // Do something with the polymorphic implementation found
            return Optional.of(foundBehavior);
        } catch (NotFoundException e) {
            // Class does not have polymorphic implementation of the method
            return Optional.empty();
        }
    }
}
