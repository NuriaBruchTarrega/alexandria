package nl.uva.alexandria.logic.metrics;

import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableMethods;
import nl.uva.alexandria.model.factories.LibraryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class MethodInvocationsCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodInvocationsCalculator.class);
    private final ClassPoolManager classPoolManager;


    public MethodInvocationsCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public DependencyTreeNode calculateMethodInvocations(DependencyTreeNode dependencyTreeNode) {
        calculateDirectCoupling(dependencyTreeNode);
        iterateTree(dependencyTreeNode);
        return dependencyTreeNode;
    }

    // MEASURE DIRECT DEPENDENCIES
    private void calculateDirectCoupling(DependencyTreeNode dependencyTreeNode) {
        // Get calls by method
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        getCallsByMethod(clientClasses, dependencyTreeNode);

        // Get polymorphic methods
        //Map<ServerMethod, Integer> mapMicPolymorphism = PolymorphismDetection.countPolymorphism(stableInvokedMethods, classPoolManager);
    }

    private void getCallsByMethod(Set<CtClass> clientClasses, DependencyTreeNode dependencyTreeNode) {
        clientClasses.forEach(clientClass -> {
            CtBehavior[] methods = clientClass.getDeclaredBehaviors();

            for (CtBehavior method : methods) {
                // getDeclaredBehaviors returns bridge methods as well, which are not needed to calculate the metric.
                // Bridge methods are marked as volatile
                if (Modifier.isVolatile(method.getModifiers())) continue;

                try {
                    method.instrument(new ExprEditor() {
                        public void edit(MethodCall methodCall) {
                            try {
                                computeBehavior(methodCall.getMethod(), dependencyTreeNode);
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        public void edit(ConstructorCall constructorCall) {
                            try {
                                computeBehavior(constructorCall.getConstructor(), dependencyTreeNode);
                            } catch (NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (CannotCompileException e) {
                    LOG.warn("Error on method.instrument\n\n{}", stackTraceToString(e));
                }
            }
        });
    }

    private void computeBehavior(CtBehavior ctBehavior, DependencyTreeNode dependencyTreeNode) {
        try {
            CtClass serverCtClass = ctBehavior.getDeclaringClass();

            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInDependency(serverCtClass)) {
                addReachableBehavior(ctBehavior, serverCtClass, dependencyTreeNode, 1, 1);
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
            if (visiting.getChildren().size() == 0) continue; // There is no dependency to calculate
            calculateTransitiveCoupling(visiting);
            toVisit.addAll(visiting.getChildren());
        }
    }

    private void calculateTransitiveCoupling(DependencyTreeNode currentLibrary) {
        Map<Integer, ReachableMethods> reachableBehaviorsAtDistance = currentLibrary.getReachableMethodsAtDistance();

        reachableBehaviorsAtDistance.forEach((distance, reachableMethods) -> {
            Map<CtBehavior, Integer> reachableMethodsMap = reachableMethods.getReachableMethods();
            reachableMethodsMap.forEach((ctBehavior, numAffectedLines) -> {
                computeApiReachableBehavior(currentLibrary, distance, ctBehavior, numAffectedLines);
            });
        });
    }

    private void computeApiReachableBehavior(DependencyTreeNode currentLibrary, Integer distance, CtBehavior ctBehavior, Integer numAffectedLines) {
        Queue<CtBehavior> toVisit = new LinkedList<>();
        Set<CtBehavior> visitedBehaviors = new HashSet<>();
        toVisit.add(ctBehavior);

        while (!toVisit.isEmpty()) {
            CtBehavior visiting = toVisit.poll();
            visitedBehaviors.add(visiting);
            Set<CtBehavior> calledBehaviorsInCurrentLibrary = findCalledBehaviors(visiting, currentLibrary, distance, numAffectedLines);
            calledBehaviorsInCurrentLibrary.forEach(calledBehavior -> {
                if (!visitedBehaviors.contains(calledBehavior)) toVisit.add(calledBehavior);
            });
        }

    }


    private Set<CtBehavior> findCalledBehaviors(CtBehavior behavior, DependencyTreeNode currentLibrary, Integer distance, Integer numAffectedLines) {
        // Obtain all method calls
        Set<CtBehavior> libraryCalledMethods = new HashSet<>();
        try {
            behavior.instrument(new ExprEditor() {
                public void edit(MethodCall methodCall) {
                    try {
                        CtMethod method = methodCall.getMethod();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(method, currentLibrary, distance, numAffectedLines);
                        behavior.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                public void edit(ConstructorCall constructorCall) {
                    try {
                        CtConstructor constructor = constructorCall.getConstructor();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(constructor, currentLibrary, distance, numAffectedLines);
                        behavior.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (CannotCompileException e) {
            LOG.info("Cannot compile\n\n{}", stackTraceToString(e));
        }

        return libraryCalledMethods;
    }

    private Optional<CtBehavior> computeBehaviorOfTransitiveDependency(CtBehavior behavior, DependencyTreeNode currentLibrary, Integer distance, Integer numAffectedLines) throws NotFoundException {
        CtClass clazz = behavior.getDeclaringClass();
        // 1. To a standard library -> discard
        if (classPoolManager.isStandardClass(clazz)) return Optional.empty();
        // 2. To a dependency -> Add it to the reachable methods of the dependency together with the numAffectedLines and distance + 1
        if (classPoolManager.isClassInDependency(clazz, currentLibrary.getLibrary().getLibraryPath())) {
            addReachableBehavior(behavior, clazz, currentLibrary, distance, numAffectedLines);
            return Optional.empty();

        }
        // 3. To a method of the library -> add to toVisit if not in visitedBehaviors
        return Optional.of(behavior);
    }

    // SHARED IN DIRECT AND TRANSITIVE
    private void addReachableBehavior(CtBehavior behavior, CtClass clazz, DependencyTreeNode currentLibrary, Integer distance, Integer numAffectedLines) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(clazz.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = currentLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent())
            libraryNode.get().addReachableApiBehaviorCall(distance, behavior, numAffectedLines);
        else LOG.warn("Library not found in tree: {}", serverLibrary.toString());
    }
}
