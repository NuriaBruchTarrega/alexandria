package nl.uva.alexandria.logic.metrics;

import javassist.*;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.ReachableMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class PolymorphismDetection {

    static void calculatePolymorphismOfDependency(DependencyTreeNode dependencyTreeNode, ClassPoolManager classPoolManager) throws NotFoundException {
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(dependencyTreeNode.getLibrary().getLibraryPath());
        Map<Integer, ReachableMethods> reachableMethodsAtDistance = dependencyTreeNode.getReachableMethodsAtDistance();

        for (CtClass libraryClass : libraryClasses) {
            reachableMethodsAtDistance.forEach((distance, reachability) -> {
                Map<CtBehavior, Set<MethodCall>> polymorphicImplementations = new HashMap<>();
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
            CtBehavior foundBehavior;
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
