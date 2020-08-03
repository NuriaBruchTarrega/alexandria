package nl.uva.alexandria.logic.metrics.inheritance;

import javassist.*;
import javassist.expr.Expr;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.ReachableBehaviors;

import java.util.*;

public class PolymorphismDetector extends InheritanceDetector {

    public PolymorphismDetector(ClassPoolManager classPoolManager) {
        super(classPoolManager);
    }

    @Override
    public void calculateInheritanceOfDependencyTreeNode(DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        updateCurrentLibrary(dependencyTreeNode.getLibrary());

        Map<Integer, ReachableBehaviors> reachableMethodsAtDistance = dependencyTreeNode.getReachableBehaviorsAtDistance();

        for (CtClass libraryClass : this.currentLibraryClasses) { // TODO: get the methods of the class
            reachableMethodsAtDistance.forEach((distance, reachability) -> {
                Map<CtBehavior, Set<Expr>> polymorphicImplementations = new HashMap<>();
                reachability.getReachableBehaviorsMap().forEach((reachableMethod, numLines) -> {
                    try {
                        Optional<CtBehavior> polymorphicImplementationOpt = findPolymorphicImplementation(libraryClass, reachableMethod);
                        polymorphicImplementationOpt.ifPresent(behavior -> polymorphicImplementations.put(behavior, numLines));
                    } catch (NotFoundException e) {
                        LOG.warn("Not found: {}", e.getMessage());
                    }
                });
                reachability.addMultipleReachableMethods(polymorphicImplementations);
            });
        }
    }

    public Set<CtBehavior> findImplementationsOfBehavior(CtBehavior ctBehavior, DependencyTreeNode dependencyTreeNode) throws NotFoundException {
        updateCurrentLibrary(dependencyTreeNode.getLibrary());
        Set<CtBehavior> implementations = new HashSet<>();

        for (CtClass libraryClass : this.currentLibraryClasses) {
            Optional<CtBehavior> implementationOpt = findPolymorphicImplementation(libraryClass, ctBehavior);
            implementationOpt.ifPresent(implementations::add);
        }

        return implementations;
    }

    private Optional<CtBehavior> findPolymorphicImplementation(CtClass libraryClass, CtBehavior reachableMethod) throws NotFoundException {
        if (!libraryClassImplementsOrExtendsReachableClass(libraryClass, reachableMethod.getDeclaringClass()))
            return Optional.empty();

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
