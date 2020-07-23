package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableFields;
import nl.uva.alexandria.model.factories.LibraryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class AggregationCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);

    private final ClassPoolManager classPoolManager;
    private final DescendantsDetector descendantsDetector;

    public AggregationCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
        this.descendantsDetector = new DescendantsDetector(classPoolManager);
    }

    public DependencyTreeNode calculateAggregationCoupling(DependencyTreeNode dependencyTreeNode) {
        // Calculate direct coupling
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        computeStableDeclaredFields(clientClasses, dependencyTreeNode);

        // Calculate transitive coupling
        iterateTree(dependencyTreeNode);

        return dependencyTreeNode;
    }

    //MEASURE DIRECT DEPENDENCIES
    private void computeStableDeclaredFields(Set<CtClass> clientClasses, DependencyTreeNode dependencyTreeNode) {
        clientClasses.forEach(clientClass -> {
            // Get all fields
            CtField[] fields = clientClass.getDeclaredFields();
            // Get all stable types
            for (CtField field : fields) {
                if (field.getGenericSignature() != null) {
                    Set<CtClass> types = findTypesInGeneric(field); // It has generic type
                    types.forEach(type -> computeFieldInDirectDependency(type, field, dependencyTreeNode));
                } else {
                    Optional<CtClass> typeOptional = findTypeInSimpleField(field); // It is a simple type
                    typeOptional.ifPresent(ctClass -> computeFieldInDirectDependency(ctClass, field, dependencyTreeNode));
                }
            }
        });
    }

    private void computeFieldInDirectDependency(CtClass clazz, CtField declaration, DependencyTreeNode dependencyTreeNode) {
        try {
            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInDependency(clazz)) {
                Set<CtField> declarations = Stream.of(declaration).collect(Collectors.toSet());
                addReachableClass(clazz, dependencyTreeNode, 1, declarations);
            }
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: " + clazz.getName());
        }

    }

    // TRANSITIVE DEPENDENCIES
    private void iterateTree(DependencyTreeNode clientLibraryNode) {
        Queue<DependencyTreeNode> toVisit = new LinkedList<>(clientLibraryNode.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyTreeNode visiting = toVisit.poll();
            if (!visiting.getReachableFieldsAtDistance().isEmpty()) findDescendantsOfReachableFields(visiting);
            if (visiting.getChildren().size() == 0 || visiting.getReachableFieldsAtDistance().isEmpty())
                continue; // There are no more dependencies
            calculateTransitiveAggregationCoupling(visiting);
            toVisit.addAll(visiting.getChildren());
        }
    }

    private void findDescendantsOfReachableFields(DependencyTreeNode currentLibrary) {
        try {
            descendantsDetector.calculateDescendantsOfDependency(currentLibrary);
        } catch (NotFoundException e) {
            LOG.error("Classes of library not found: {}", stackTraceToString(e));
        }
    }

    private void calculateTransitiveAggregationCoupling(DependencyTreeNode currentLibrary) {
        Map<Integer, ReachableFields> reachableFieldsAtDistance = currentLibrary.getReachableFieldsAtDistance();

        reachableFieldsAtDistance.forEach((distance, reachableFields) -> {
            Map<CtClass, Set<CtField>> reachableFieldsMap = reachableFields.getReachableFields();
            reachableFieldsMap.forEach(((ctClass, declarations) -> computeApiReachableField(currentLibrary, distance, ctClass, declarations)));
        });

    }

    private void computeApiReachableField(DependencyTreeNode currentLibrary, Integer distance, CtClass ctClass, Set<CtField> declarations) {
        Queue<CtClass> toVisit = new LinkedList<>();
        Set<CtClass> visitedClasses = new HashSet<>();
        toVisit.add(ctClass);

        while (!toVisit.isEmpty()) {
            CtClass visiting = toVisit.poll();
            if (visitedClasses.contains(visiting)) continue;
            visitedClasses.add(visiting);

            if (Modifier.isAbstract(visiting.getModifiers())) {
                Set<CtClass> descendants = findDescendantsOfClass(visiting, currentLibrary);
                toVisit.addAll(descendants);
            }

            Set<CtClass> declaredFieldsInCurrentLibrary = findDeclaredFields(visiting, currentLibrary, distance, declarations);
            declaredFieldsInCurrentLibrary.forEach(declaredField -> {
                if (!visitedClasses.contains(declaredField)) toVisit.add(declaredField);
            });
        }
    }

    private Set<CtClass> findDescendantsOfClass(CtClass ctClass, DependencyTreeNode dependencyTreeNode) {
        return new HashSet<>();
    }

    private Set<CtClass> findDeclaredFields(CtClass visiting, DependencyTreeNode currentLibrary, Integer distance, Set<CtField> declarations) {
        Set<CtClass> libraryDeclaredFields = new HashSet<>();

        CtField[] fields = visiting.getDeclaredFields();

        try {
            for (CtField field : fields) {
                if (field.getGenericSignature() != null) {
                    Set<CtClass> types = findTypesInGeneric(field);
                    for (CtClass type : types) {
                        Optional<CtClass> fieldToVisitOpt = computeFieldInTransitiveDependency(type, currentLibrary, distance, declarations);
                        fieldToVisitOpt.ifPresent(libraryDeclaredFields::add);
                    }
                } else {
                    Optional<CtClass> typeOptional = findTypeInSimpleField(field);
                    if (typeOptional.isPresent()) {
                        Optional<CtClass> fieldToVisitOpt = computeFieldInTransitiveDependency(typeOptional.get(), currentLibrary, distance, declarations);
                        fieldToVisitOpt.ifPresent(libraryDeclaredFields::add);
                    }
                }
            }
        } catch (NotFoundException e) {
            LOG.warn("Not found: {}", stackTraceToString(e));
        }

        return libraryDeclaredFields;
    }

    private Optional<CtClass> computeFieldInTransitiveDependency(CtClass field, DependencyTreeNode currentLibrary, Integer distance, Set<CtField> declarations) throws NotFoundException {
        // 1. From a standard library -> discard
        if (classPoolManager.isStandardClass(field)) return Optional.empty();
        // 2. From a dependency -> add to reachableMethods of the dependency
        if (classPoolManager.isClassInDependency(field, currentLibrary.getLibrary().getLibraryPath())) {
            addReachableClass(field, currentLibrary, distance + 1, declarations);
            return Optional.empty();
        }
        // 3. From the current library -> return to visit in the future
        return Optional.of(field);
    }

    // SHARED IN DIRECT AND TRANSITIVE
    private void addReachableClass(CtClass ctClass, DependencyTreeNode dependencyTreeNode, Integer distance, Set<CtField> declarations) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = dependencyTreeNode.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableApiField(distance, ctClass, declarations);
        } else LOG.warn("Library not found in tree: {}", serverLibrary.toString());
    }

    // Methods to compute the different types of field
    private Set<CtClass> findTypesInGeneric(CtField field) {
        String gen = field.getGenericSignature();
        List<String> classNames = ClassNameUtils.getClassNamesFromGenericSignature(gen);
        return classPoolManager.getClassesFromClassNames(classNames);
    }

    private Optional<CtClass> findTypeInSimpleField(CtField field) {
        try {
            CtClass serverClass = field.getType();
            if (serverClass.isPrimitive()) return Optional.empty(); // Ignore primitives
            if (serverClass.isArray()) return getTypeOfArray(field);
            return Optional.of(serverClass);
        } catch (NotFoundException e) {
            LOG.warn("Not able to find class of field: " + field.getSignature());
        }
        return Optional.empty();
    }

    private Optional<CtClass> getTypeOfArray(CtField field) throws NotFoundException {
        String signature = field.getSignature();
        String className = ClassNameUtils.signatureToClassName(signature);

        if (className.length() == 0) return Optional.empty();
        return Optional.ofNullable(classPoolManager.getClassFromClassName(className));
    }
}
