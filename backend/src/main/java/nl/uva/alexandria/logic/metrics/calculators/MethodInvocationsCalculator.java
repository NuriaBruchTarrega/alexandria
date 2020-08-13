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

    public MethodInvocationsCalculator(ClassPoolManager classPoolManager, DependencyTreeNode rootLibrary) {
        super(classPoolManager, new PolymorphismDetector(classPoolManager), rootLibrary);
    }

    // MEASURE DIRECT DEPENDENCIES
    @Override
    public void visitClientLibrary() {
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        findDependencyUsageInBehaviors(clientClasses);
    }

    private void findDependencyUsageInBehaviors(Set<CtClass> clientClasses) {
        clientClasses.forEach(clientClass -> {
            CtBehavior[] behaviors = clientClass.getDeclaredBehaviors();

            for (CtBehavior behavior : behaviors) {
                // getDeclaredBehaviors returns bridge methods as well, which are not needed to calculate the metric.
                // Bridge methods are marked as volatile
                if (Modifier.isVolatile(behavior.getModifiers())) continue;

                try {
                    findDependencyUsageInBehavior(behavior);
                } catch (CannotCompileException e) {
                    LOG.warn("Error on behavior.instrument\n\n{}", stackTraceToString(e));
                } catch (NotFoundException e) {
                    LOG.warn("Error finding parameter or return types of behavior: {}", e.getMessage());
                }
            }
        });
    }

    private void findDependencyUsageInBehavior(CtBehavior behavior) throws CannotCompileException, NotFoundException {
        CtClass[] parameterTypes = behavior.getParameterTypes();
        for (CtClass parameterType : parameterTypes) {
            computeUsedClass(parameterType);
        }

        if (behavior instanceof CtMethod) {
            CtClass returnType = ((CtMethod) behavior).getReturnType();
            computeUsedClass(returnType);
        }

        findMethodCallsToDependencies(behavior);
    }

    private void findMethodCallsToDependencies(CtBehavior behavior) throws CannotCompileException {
        if (behavior.getDeclaringClass().isFrozen()) behavior.getDeclaringClass().defrost();
        behavior.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) {
                try {
                    computeCalledBehavior(methodCall.getMethod(), methodCall);
                } catch (NotFoundException e) {
                    LOG.warn("Method not found: {}", stackTraceToString(e));
                }
            }

            @Override
            public void edit(ConstructorCall constructorCall) {
                try {
                    computeCalledBehavior(constructorCall.getConstructor(), constructorCall);
                } catch (NotFoundException e) {
                    LOG.warn("Constructor not found: {}", stackTraceToString(e));
                }
            }

            @Override
            public void edit(NewExpr newExpr) {
                try {
                    computeCalledBehavior(newExpr.getConstructor(), newExpr);
                } catch (NotFoundException e) {
                    LOG.warn("Constructor not found: {}", stackTraceToString(e));
                }
            }
        });
    }

    private void computeCalledBehavior(CtBehavior ctBehavior, Expr methodCall) {
        try {
            CtClass serverCtClass = ctBehavior.getDeclaringClass();

            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInDependency(serverCtClass)) {
                Set<Expr> reachableFrom = Stream.of(methodCall).collect(Collectors.toSet());
                addReachableBehavior(ctBehavior, serverCtClass, 1, reachableFrom);
            }
        } catch (NotFoundException e) {
            LOG.warn("Class not found\n\n{}", stackTraceToString(e));
        }
    }

    private void computeUsedClass(CtClass ctClass) {
        try {
            if (classPoolManager.isClassInDependency(ctClass)) {
                addReachableClass(ctClass);
            }
        } catch (NotFoundException e) {
            LOG.warn("Class not found\n\n{}", stackTraceToString(e));
        }
    }

    // MEASURE TRANSITIVE DEPENDENCIES
    @Override
    public void visitServerLibrary(DependencyTreeNode currentLibrary) {
        Map<Integer, ReachableBehaviors> reachableBehaviorsAtDistance = currentLibrary.getReachableApiBehaviorsAtDistance();

        reachableBehaviorsAtDistance.forEach((distance, reachableBehaviors) -> {
            Map<CtBehavior, Set<Expr>> reachableMethodsMap = reachableBehaviors.getReachableBehaviorsMap();
            reachableMethodsMap.forEach((ctBehavior, reachableFrom) -> computeApiReachableBehavior(currentLibrary, distance, ctBehavior, reachableFrom));
        });
    }

    private void findPolymorphicImplementationsOfReachableMethods(DependencyTreeNode visiting) {
        try {
            inheritanceDetector.calculateInheritanceOfDependencyTreeNode(visiting);
        } catch (NotFoundException e) {
            LOG.warn("Classes of library not found: {}", stackTraceToString(e));
        }
    }

    private void computeApiReachableBehavior(DependencyTreeNode currentLibrary, Integer distance, CtBehavior ctBehavior, Set<Expr> reachableFrom) {
        Queue<CtBehavior> toVisit = new ArrayDeque<>();
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
            if (behavior.getDeclaringClass().isFrozen()) behavior.getDeclaringClass().defrost();

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
            addReachableBehavior(behavior, clazz, distance + 1, reachableFrom);
            return Optional.empty();
        }
        // 3. To a method of the library -> add to toVisit if not in visitedBehaviors
        return Optional.of(behavior);
    }

    // SHARED IN DIRECT AND TRANSITIVE
    private void addReachableBehavior(CtBehavior behavior, CtClass clazz, Integer distance, Set<Expr> reachableFrom) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(clazz.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableApiBehavior(distance, behavior, reachableFrom);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }

    private void addReachableClass(CtClass ctClass) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableClass(ctClass);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }
}
