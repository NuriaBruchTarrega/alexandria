package nl.uva.alexandria.logic.metrics;

import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableMethods;
import nl.uva.alexandria.model.ServerMethod;
import nl.uva.alexandria.model.factories.LibraryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class MethodInvocationsCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodInvocationsCalculator.class);
    private final ClassPoolManager classPoolManager;
    //private Map<ServerMethod, Integer> stableInvokedMethods = new HashMap<>();


    public MethodInvocationsCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public Map<ServerMethod, Integer> calculateMethodInvocations(DependencyTreeNode dependencyTreeNode) {
        //this.clientLibrary = dependencyTreeNode.getLibrary();
        calculateDirectCoupling(dependencyTreeNode);
        iterateTree(dependencyTreeNode);
        return null;
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
                addReachableBehavior(ctBehavior, serverCtClass, dependencyTreeNode);
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
                computeApiReachableBehavior(distance, ctBehavior, numAffectedLines);
            });
        });
//        Queue<CtBehavior> reachableBehaviors = new LinkedList<>(currentLibrary.getReachableApiBehaviors());
//        //Set<CtBehavior> visitedBehaviors = new HashSet<>();
//
//        while (!reachableBehaviors.isEmpty()) {
//            CtBehavior behavior = reachableBehaviors.poll();
//
//            //visitedBehaviors.add(behavior); // Add behavior to visited so it is not computed again
//
////            Set<CtBehavior> calledBehaviors = findCalledBehaviors(behavior, currentLibrary);
////            calledBehaviors.forEach(calledBehavior -> {
////                // Only include the called behaviors that have not been visited
////                if (!visitedBehaviors.contains(calledBehavior)) reachableBehaviors.add(calledBehavior);
////            });
//        }
    }

    private void computeApiReachableBehavior(Integer distance, CtBehavior ctBehavior, Integer numAffectedLines) {
        Queue<CtBehavior> toVisit = new LinkedList<>();
        Set<CtBehavior> visitedBehaviors = new HashSet<>();
        toVisit.add(ctBehavior);

        while (!toVisit.isEmpty()) {
            CtBehavior visiting = toVisit.poll();

        }

    }


    private Set<CtBehavior> findCalledBehaviors(CtBehavior behavior, DependencyTreeNode currentLibrary) {
        Set<CtBehavior> libraryCalledMethods = new HashSet<>();
        try {
            behavior.instrument(new ExprEditor() {
                public void edit(MethodCall methodCall) {
                    try {
                        CtMethod method = methodCall.getMethod();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(method, currentLibrary);
                        behavior.ifPresent(libraryCalledMethods::add);
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                public void edit(ConstructorCall constructorCall) {
                    try {
                        CtConstructor constructor = constructorCall.getConstructor();
                        Optional<CtBehavior> behavior = computeBehaviorOfTransitiveDependency(constructor, currentLibrary);
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

    private Optional<CtBehavior> computeBehaviorOfTransitiveDependency(CtBehavior behavior, DependencyTreeNode currentLibrary) throws NotFoundException {
        CtClass clazz = behavior.getDeclaringClass();
        if (!classPoolManager.isNotStandardClass(clazz)) return Optional.empty();
        if (classPoolManager.isClassInDependency(clazz, currentLibrary.getLibrary().getLibraryPath())) {
            addReachableBehavior(behavior, clazz, currentLibrary);
            return Optional.empty();
        } else return Optional.of(behavior);
    }

    // SHARED IN DIRECT AND TRANSITIVE
    private void addReachableBehavior(CtBehavior behavior, CtClass clazz, DependencyTreeNode currentLibrary) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(clazz.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = currentLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) libraryNode.get().addReachableApiBehaviorCall(behavior);
        else LOG.warn("Library not found in tree: {}", serverLibrary.toString());
    }
}
