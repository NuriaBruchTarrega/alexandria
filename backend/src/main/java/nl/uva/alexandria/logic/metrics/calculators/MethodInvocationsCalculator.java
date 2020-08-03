package nl.uva.alexandria.logic.metrics.calculators;

import javassist.*;
import javassist.expr.*;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.PolymorphismDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableBehaviors;
import nl.uva.alexandria.model.factories.LibraryFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class MethodInvocationsCalculator extends MetricCalculator {

    public MethodInvocationsCalculator(ClassPoolManager classPoolManager) {
        super(classPoolManager, new PolymorphismDetector(classPoolManager));
    }

    public DependencyTreeNode calculateMetric(DependencyTreeNode dependencyTreeNode) {
        calculateDirectCoupling(dependencyTreeNode);
        iterateTree(dependencyTreeNode);
        return dependencyTreeNode;
    }

    // MEASURE DIRECT DEPENDENCIES
    private void calculateDirectCoupling(DependencyTreeNode dependencyTreeNode) {
        // Get calls by method
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        computeCallsToDependencies(clientClasses, dependencyTreeNode); //TODO: refactor method name
    }

    private void computeCallsToDependencies(Set<CtClass> clientClasses, DependencyTreeNode dependencyTreeNode) {
        clientClasses.forEach(clientClass -> {
            CtBehavior[] behaviors = clientClass.getDeclaredBehaviors();

            for (CtBehavior behavior : behaviors) {
                // getDeclaredBehaviors returns bridge methods as well, which are not needed to calculate the metric.
                // Bridge methods are marked as volatile
                if (Modifier.isVolatile(behavior.getModifiers())) continue;

                try {
                    instrumentBehaviorDirectCoupling(behavior, dependencyTreeNode);
                } catch (CannotCompileException e) {
                    LOG.warn("Error on behavior.instrument\n\n{}", stackTraceToString(e));
                }
            }
        });
    }

    private void instrumentBehaviorDirectCoupling(CtBehavior method, DependencyTreeNode dependencyTreeNode) throws CannotCompileException {
        method.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) {
                try {
                    computeBehavior(methodCall.getMethod(), methodCall, dependencyTreeNode);
                } catch (NotFoundException e) {
                    LOG.warn("Method not found: {}", stackTraceToString(e));
                }
            }

            @Override
            public void edit(ConstructorCall constructorCall) {
                try {
                    computeBehavior(constructorCall.getConstructor(), constructorCall, dependencyTreeNode);
                } catch (NotFoundException e) {
                    LOG.warn("Constructor not found: {}", stackTraceToString(e));
                }
            }

            @Override
            public void edit(NewExpr newExpr) {
                try {
                    computeBehavior(newExpr.getConstructor(), newExpr, dependencyTreeNode);
                } catch (NotFoundException e) {
                    LOG.warn("Not found: {}", stackTraceToString(e));
                }
            }
        });
    }

    private void computeBehavior(CtBehavior ctBehavior, Expr methodCall, DependencyTreeNode dependencyTreeNode) {
        try {
            CtClass serverCtClass = ctBehavior.getDeclaringClass();

            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInDependency(serverCtClass)) {
                Set<Expr> reachableFrom = Stream.of(methodCall).collect(Collectors.toSet());
                addReachableBehavior(ctBehavior, serverCtClass, dependencyTreeNode, 1, reachableFrom);
            }
        } catch (NotFoundException e) {
            LOG.warn("Class not found\n\n{}", stackTraceToString(e));
        }
    }

    // MEASURE TRANSITIVE DEPENDENCIES
    private void iterateTree(DependencyTreeNode root) {
        Queue<DependencyTreeNode> toVisit = new LinkedList<>(root.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            // If there are any reachable methods, let's find all the polymorphic implementations
            if (!visiting.getReachableBehaviorsAtDistance().isEmpty())
                findPolymorphicImplementationsOfReachableMethods(visiting);
            if (visiting.getReachableBehaviorsAtDistance().isEmpty() || visiting.getChildren().isEmpty())
                continue; // There is no dependency to calculate
            calculateTransitiveCoupling(visiting);
            toVisit.addAll(visiting.getChildren());
        }
    }

    private void findPolymorphicImplementationsOfReachableMethods(DependencyTreeNode visiting) {
        try {
            inheritanceDetector.calculateInheritanceOfDependencyTreeNode(visiting);
        } catch (NotFoundException e) {
            LOG.warn("Classes of library not found: {}", stackTraceToString(e));
        }
    }

    private void calculateTransitiveCoupling(DependencyTreeNode currentLibrary) {
        Map<Integer, ReachableBehaviors> reachableBehaviorsAtDistance = currentLibrary.getReachableBehaviorsAtDistance();

        reachableBehaviorsAtDistance.forEach((distance, reachableBehaviors) -> {
            Map<CtBehavior, Set<Expr>> reachableMethodsMap = reachableBehaviors.getReachableBehaviorsMap();
            reachableMethodsMap.forEach((ctBehavior, reachableFrom) -> computeApiReachableBehavior(currentLibrary, distance, ctBehavior, reachableFrom));
        });
    }

    private void computeApiReachableBehavior(DependencyTreeNode currentLibrary, Integer distance, CtBehavior ctBehavior, Set<Expr> reachableFrom) {
        Queue<CtBehavior> toVisit = new LinkedList<>();
        Set<CtBehavior> visitedBehaviors = new HashSet<>();
        toVisit.add(ctBehavior);

        while (!toVisit.isEmpty()) {
            CtBehavior visiting = toVisit.poll();
            if (visitedBehaviors.contains(visiting)) continue;
            visitedBehaviors.add(visiting);

            if (Modifier.isAbstract(visiting.getModifiers())) {
                Set<CtBehavior> implementations = findImplementationsOfBehavior(visiting, currentLibrary);
                toVisit.addAll(implementations);
            }

            Set<CtBehavior> calledBehaviorsInCurrentLibrary = findCalledBehaviors(visiting, currentLibrary, distance, reachableFrom);
            toVisit.addAll(calledBehaviorsInCurrentLibrary);
        }
    }

    private Set<CtBehavior> findImplementationsOfBehavior(CtBehavior ctBehavior, DependencyTreeNode dependencyTreeNode) {
        try {
            return ((PolymorphismDetector) inheritanceDetector).findImplementationsOfBehavior(ctBehavior, dependencyTreeNode);
        } catch (NotFoundException e) {
            LOG.warn("Classes of library not found: {}", stackTraceToString(e));
        }
        return new HashSet<>();
    }

    private Set<CtBehavior> findCalledBehaviors(CtBehavior behavior, DependencyTreeNode currentLibrary, Integer distance, Set<Expr> reachableFrom) {
        // Obtain all method calls
        Set<CtBehavior> libraryCalledMethods = new HashSet<>();
        try {
            behavior.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) {
                    try {
                        CtMethod method = methodCall.getMethod();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(method, currentLibrary, distance, reachableFrom);
                        behavior.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        LOG.warn("Not found: {}", stackTraceToString(e));
                    }
                }

                @Override
                public void edit(ConstructorCall constructorCall) {
                    try {
                        CtConstructor constructor = constructorCall.getConstructor();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(constructor, currentLibrary, distance, reachableFrom);
                        behavior.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        LOG.warn("Not found: {}", stackTraceToString(e));
                    }
                }

                @Override
                public void edit(NewExpr newExpr) {
                    try {
                        CtConstructor constructor = newExpr.getConstructor();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(constructor, currentLibrary, distance, reachableFrom);
                        behavior.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        LOG.warn("Not found: {}", stackTraceToString(e));
                    }
                }
            });
        } catch (CannotCompileException e) {
            LOG.info("Cannot compile\n\n{}", stackTraceToString(e));
        }

        return libraryCalledMethods;
    }

    private Optional<CtBehavior> computeBehaviorOfTransitiveDependency(CtBehavior behavior, DependencyTreeNode currentLibrary, Integer distance, Set<Expr> reachableFrom) throws NotFoundException {
        CtClass clazz = behavior.getDeclaringClass();
        // 1. To a standard library -> discard
        if (classPoolManager.isStandardClass(clazz)) return Optional.empty();
        // 2. To a dependency -> Add it to the reachable methods of the dependency together with the reachableFrom and distance + 1
        if (classPoolManager.isClassInDependency(clazz, currentLibrary.getLibrary().getLibraryPath())) {
            addReachableBehavior(behavior, clazz, currentLibrary, distance + 1, reachableFrom);
            return Optional.empty();
        }
        // 3. To a method of the library -> add to toVisit if not in visitedBehaviors
        return Optional.of(behavior);
    }

    // SHARED IN DIRECT AND TRANSITIVE
    private void addReachableBehavior(CtBehavior behavior, CtClass clazz, DependencyTreeNode currentLibrary, Integer distance, Set<Expr> reachableFrom) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(clazz.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = currentLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableApiBehavior(distance, behavior, reachableFrom);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary.toString());
        }
    }
}
