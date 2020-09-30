package nl.uva.alexandria.logic.metrics.calculators;

import javassist.*;
import javassist.expr.*;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.PolymorphismDetector;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableBehaviors;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class MethodInvocationsCalculator extends MetricCalculator {

    private final AnnotationsCalculator annotationsCalculator;

    public MethodInvocationsCalculator(ClassPoolManager classPoolManager, DependencyTreeNode rootLibrary) {
        super(classPoolManager, new PolymorphismDetector(classPoolManager), rootLibrary);
        this.annotationsCalculator = new AnnotationsCalculator(classPoolManager, rootLibrary);
    }

    // PUBLIC METHODS
    @Override
    public void visitClientLibrary() {
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        findDependencyUsageInBehaviors(clientClasses);
    }

    @Override
    public void findInheritanceOfServerLibrary(DependencyTreeNode currentLibrary) {
        try {
            inheritanceDetector.calculateInheritanceOfDependencyTreeNode(currentLibrary);
        } catch (NotFoundException e) {
            LOG.warn("Classes of library not found: {}", stackTraceToString(e));
        }
    }

    @Override
    public void visitServerLibrary(DependencyTreeNode currentLibrary) {
        Map<Integer, ReachableBehaviors> reachableBehaviorsAtDistance = currentLibrary.getReachableApiBehaviorsAtDistance();

        reachableBehaviorsAtDistance.forEach((distance, reachableBehaviors) -> {
            Map<CtBehavior, Set<Expr>> reachableMethodsMap = reachableBehaviors.getReachableBehaviorsMap();
            reachableMethodsMap.forEach((ctBehavior, reachableFrom) -> computeApiReachableBehavior(currentLibrary, distance, ctBehavior, reachableFrom));
        });
    }


    // PRIVATE METHODS
    // Used in visitClientLibrary
    private void findDependencyUsageInBehaviors(Set<CtClass> clientClasses) {
        for (CtClass clientClass : clientClasses) {
            CtBehavior[] behaviors = clientClass.getDeclaredBehaviors();

            for (CtBehavior behavior : behaviors) {
                // getDeclaredBehaviors returns bridge methods as well, which are not needed to calculate the metric.
                // Bridge methods are marked as volatile
                if (Modifier.isVolatile(behavior.getModifiers())) continue;

                try {
                    findMethodCallsToDependencies(behavior);
                    findDependencyUsageInParametersOrReturn(behavior, 0, this.rootLibrary);
                    findDependencyUsageInExceptions(behavior, 0, this.rootLibrary);
                    findDependencyUsageFieldAccess(behavior, 0, this.rootLibrary);
                    this.annotationsCalculator.findAnnotations(behavior, 0, this.rootLibrary);
                } catch (CannotCompileException e) {
                    LOG.warn("Error on behavior.instrument\n\n{}", stackTraceToString(e));
                }
            }
        }
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

    // Used in visitServerLibrary

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

            Set<CtBehavior> calledBehaviorsInCurrentLibrary = findDependencyUsageReachableBehavior(visiting, distance, reachableFrom, currentLibrary);
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

    private Set<CtBehavior> findDependencyUsageReachableBehavior(CtBehavior behavior, int distance, Set<Expr> reachableFrom, DependencyTreeNode currentLibrary) {
        findDependencyUsageInParametersOrReturn(behavior, distance, currentLibrary);
        findDependencyUsageInExceptions(behavior, distance, currentLibrary);
        findDependencyUsageFieldAccess(behavior, distance, currentLibrary);
        this.annotationsCalculator.findAnnotations(behavior, distance, currentLibrary);
        return findCalledBehaviors(behavior, distance, reachableFrom, currentLibrary);
    }

    private Set<CtBehavior> findCalledBehaviors(CtBehavior behavior, Integer distance, Set<Expr> reachableFrom, DependencyTreeNode currentLibrary) {
        // Obtain all method calls
        Set<CtBehavior> libraryCalledMethods = new HashSet<>();
        try {
            if (behavior.getDeclaringClass().isFrozen()) behavior.getDeclaringClass().defrost();

            behavior.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall methodCall) {
                    try {
                        CtMethod method = methodCall.getMethod();
                        Optional<CtBehavior> behaviorOpt = computeBehaviorOfTransitiveDependency(method, currentLibrary, distance, reachableFrom);
                        behaviorOpt.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        LOG.warn("Not found: {}", stackTraceToString(e));
                    }
                }

                @Override
                public void edit(ConstructorCall constructorCall) {
                    try {
                        CtConstructor constructor = constructorCall.getConstructor();
                        Optional<CtBehavior> behaviorOpt = computeBehaviorOfTransitiveDependency(constructor, currentLibrary, distance, reachableFrom);
                        behaviorOpt.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        LOG.warn("Not found: {}", stackTraceToString(e));
                    }
                }

                @Override
                public void edit(NewExpr newExpr) {
                    try {
                        CtConstructor constructor = newExpr.getConstructor();
                        Optional<CtBehavior> behaviorOpt = computeBehaviorOfTransitiveDependency(constructor, currentLibrary, distance, reachableFrom);
                        behaviorOpt.ifPresent(libraryCalledMethods::add);
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
        // 3. To a method of the library -> add to reachable behaviors and to toVisit
        currentLibrary.addReachableBehavior(behavior, distance);
        return Optional.of(behavior);
    }

    // Shared for visitClientLibrary and visitServerLibrary
    private void findDependencyUsageInParametersOrReturn(CtBehavior behavior, int distance, DependencyTreeNode currentLibrary) {
        try {
            CtClass[] parameterTypes = behavior.getParameterTypes();
            for (CtClass parameterType : parameterTypes) {
                computeUsedClass(parameterType, distance, currentLibrary);
            }
        } catch (NotFoundException e) {
            LOG.warn("Error finding parameter types of behavior: {}", e.getMessage());
        }

        try {
            if (behavior instanceof CtMethod) {
                CtClass returnType = ((CtMethod) behavior).getReturnType();
                computeUsedClass(returnType, distance, currentLibrary);
            }
        } catch (NotFoundException e) {
            LOG.warn("Error finding return type of behavior: {}", e.getMessage());
        }
    }

    private void findDependencyUsageInExceptions(CtBehavior behavior, int distance, DependencyTreeNode currentLibrary) {
        try {
            CtClass[] exceptionTypes = behavior.getExceptionTypes();

            for (CtClass exceptionType : exceptionTypes) {
                computeUsedClass(exceptionType, distance, currentLibrary);
            }
        } catch (NotFoundException e) {
            LOG.warn("Error retrieving exception types: {}", e.getMessage());
        }
    }

    private void findDependencyUsageFieldAccess(CtBehavior behavior, int distance, DependencyTreeNode currentLibrary) {
        if (behavior.getDeclaringClass().isFrozen()) behavior.getDeclaringClass().defrost();
        try {
            behavior.instrument(new ExprEditor() {
                @Override
                public void edit(FieldAccess fieldAccess) {
                    try {
                        CtClass fieldType = fieldAccess.getField().getType();
                        computeUsedClass(fieldType, distance, currentLibrary);
                    } catch (NotFoundException e) {
                        LOG.warn("Method not found: {}", stackTraceToString(e));
                    }
                }
            });
        } catch (CannotCompileException e) {
            LOG.error("Error on field access: {}", e.getMessage());
        }
    }

    private void computeUsedClass(CtClass ctClass, int distance, DependencyTreeNode currentLibrary) {
        try {
            if (ctClass.isArray()) ctClass = getClassInArray(ctClass);

            if (classPoolManager.isStandardClass(ctClass)) return;
            if (classPoolManager.isClassInDependency(ctClass, currentLibrary.getLibrary().getLibraryPath())) {
                addReachableClass(ctClass, distance + 1);
            } else if (!currentLibrary.equals(this.rootLibrary)) {
                currentLibrary.addReachableClass(ctClass, distance);
            }
        } catch (NotFoundException e) {
            LOG.warn("Class not found\n\n{}", stackTraceToString(e));
        }
    }

    private void addReachableBehavior(CtBehavior behavior, CtClass clazz, int distance, Set<Expr> reachableFrom) throws NotFoundException {
        Library serverLibrary = Library.fromClassPath(clazz.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableApiBehavior(distance, behavior, reachableFrom);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }

    private void addReachableClass(CtClass ctClass, int distance) throws NotFoundException {
        Library serverLibrary = Library.fromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableClass(ctClass, distance);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }
}
