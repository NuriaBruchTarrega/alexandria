package nl.uva.alexandria.logic.metrics.calculators;

import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.metrics.inheritance.DescendantsDetector;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ReachableClasses;
import nl.uva.alexandria.model.factories.LibraryFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class AggregationCalculator extends MetricCalculator {

    public AggregationCalculator(ClassPoolManager classPoolManager, DependencyTreeNode rootLibrary) {
        super(classPoolManager, new DescendantsDetector(classPoolManager), rootLibrary);
    }

    // DIRECT DEPENDENCIES

    /**
     * Iterate through the classes of the client library to find usages of classes of the dependencies as:
     * - Superclass
     * - Implemented interface
     * - Field type
     */
    @Override
    public void visitClientLibrary() {
        // Get calls by method
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        clientClasses.forEach(clientClass -> {
            findUsageInSuperClassAndInterfaces(clientClass);
            findUsageInFields(clientClass);
        });
    }

    /**
     * Find, in a class of the client library, usages of a dependency as superclass or as interface
     *
     * @param ctClass a class of the client library
     */
    private void findUsageInSuperClassAndInterfaces(CtClass ctClass) {
        try {
            CtClass superClass = ctClass.getSuperclass();
            if (superClass != null) {
                computeUsedClassDirect(superClass, null);
            }
        } catch (NotFoundException e) {
            LOG.warn("Superclass not found: {}", e.getMessage());
        }

        try {
            CtClass[] interfaces = ctClass.getInterfaces();
            for (CtClass interfaze : interfaces) {
                computeUsedClassDirect(interfaze, null);
            }
        } catch (NotFoundException e) {
            LOG.warn("Interfaces not found: {}", e.getMessage());
        }
    }

    /**
     * Find, in a class of the client library, usages of a dependency as field types.
     * @param ctClass a class of the client library
     */
    private void findUsageInFields(CtClass ctClass) {
        CtField[] fields = ctClass.getDeclaredFields();
        for (CtField field : fields) {
            if (field.getGenericSignature() != null) {
                Set<CtClass> types = findTypesInGeneric(field); // It has generic type
                types.forEach(type -> computeUsedClassDirect(type, field));
            } else {
                Optional<CtClass> typeOptional = findTypeInSimpleField(field); // It is a simple type
                typeOptional.ifPresent(type -> computeUsedClassDirect(type, field));
            }
        }
    }

    /**
     * Receives a used class in the client library.
     * If the class is implemented in a dependency of the client library, adds it to the reachable classes of the library
     * If the class is used in a field declaration, is added to the reachable API Fields
     * @param usedClass type included in the field declaration
     * @param declaration field declaration - null if it is not a field declaration
     */
    private void computeUsedClassDirect(CtClass usedClass, CtField declaration) {
        try {
            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInDependency(usedClass)) {
                if (declaration != null) {
                    Set<CtField> declarations = Stream.of(declaration).collect(Collectors.toSet());
                    addReachableFieldClass(usedClass, 1, declarations);
                } else {
                    addReachableClass(usedClass);
                }
            }
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: {}", usedClass.getName());
        }
    }

    // TRANSITIVE DEPENDENCIES

    /**
     * For a given library, find all the descendants of the reachable classes
     * @param currentLibrary the DependencyTreeNode representing the given library
     */
    @Override
    public void findInheritanceOfServerLibrary(DependencyTreeNode currentLibrary) {
        try {
            inheritanceDetector.calculateInheritanceOfDependencyTreeNode(currentLibrary);
        } catch (NotFoundException e) {
            LOG.error("Classes of library not found: {}", stackTraceToString(e));
        }
    }

    /**
     * Given the reachable classes of the API of a library, contained in the DependencyTreeNode of the library.
     * Find all the reachable classes and usage of dependencies.
     * Compute aggregation coupling.
     * @param currentLibrary the DependencyTreeNode representing the library
     */
    @Override
    public void visitServerLibrary(DependencyTreeNode currentLibrary) {
        Map<Integer, ReachableClasses> reachableFieldsAtDistance = currentLibrary.getReachableApiFieldClassesAtDistance();

        reachableFieldsAtDistance.forEach((distance, reachableClasses) -> {
            Map<CtClass, Set<CtField>> reachableClassesMap = reachableClasses.getReachableClassesMap();
            reachableClassesMap.forEach((ctClass, declarations) -> computeAggregationCouplingOfClass(currentLibrary, distance, ctClass, declarations));
        });

        findAllReachableClasses(currentLibrary);
    }

    // AGGREGATION COUPLING

    /**
     * Calculates the aggregation coupling created by a given class of a given library
     *
     * @param currentLibrary the DependencyTreeNode representing the library
     * @param distance       the distance at which the class is reachable
     * @param ctClass        reachable class of the given library
     * @param declarations   set of field declarations from which the class is reachable
     */
    private void computeAggregationCouplingOfClass(DependencyTreeNode currentLibrary, Integer distance, CtClass ctClass, Set<CtField> declarations) {
        Queue<CtClass> toVisit = new ArrayDeque<>();
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
            toVisit.addAll(declaredFieldsInCurrentLibrary);
        }
    }

    private Set<CtClass> findDescendantsOfClass(CtClass ctClass, DependencyTreeNode dependencyTreeNode) {
        try {
            return ((DescendantsDetector) inheritanceDetector).findDescendantsOfClass(ctClass, dependencyTreeNode);
        } catch (NotFoundException e) {
            LOG.warn("Classes of library not found: {}", stackTraceToString(e));
        }
        return new HashSet<>();
    }

    private Set<CtClass> findDeclaredFields(CtClass visiting, DependencyTreeNode currentLibrary, Integer distance, Set<CtField> declarations) {
        Set<CtClass> libraryDeclaredFields = new HashSet<>();

        CtField[] fields = visiting.getDeclaredFields();

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

        return libraryDeclaredFields;
    }

    private Optional<CtClass> computeFieldInTransitiveDependency(CtClass field, DependencyTreeNode currentLibrary, Integer distance, Set<CtField> declarations) {
        try {
            // 1. From a standard library -> discard
            if (classPoolManager.isStandardClass(field)) return Optional.empty();
            // 2. From a dependency -> add to reachableMethods of the dependency
            if (classPoolManager.isClassInDependency(field, currentLibrary.getLibrary().getLibraryPath())) {
                addReachableFieldClass(field, distance + 1, declarations);
                return Optional.empty();
            }
            // 3. From the current library -> return to visit in the future and add to reachable classes
            currentLibrary.addReachableClass(field);
            return Optional.of(field);
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: {}", field.getName());
            return Optional.empty();
        }
    }

    // FIND ALL REACHABLE CLASSES
    private void findAllReachableClasses(DependencyTreeNode currentLibrary) {
        Queue<CtClass> toVisit = new ArrayDeque<>(currentLibrary.getReachableClasses());
        Set<CtClass> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {
            CtClass visiting = toVisit.poll();
            if (visited.contains(visiting)) continue;
            visited.add(visiting);

            // Find superclasses and implemented interfaces
            Set<CtClass> superClassesAndInterfaces = findSuperClassAndInterfacesTransitive(visiting, currentLibrary);
            toVisit.addAll(superClassesAndInterfaces);
            // Find fields
            Set<CtClass> fields = findFieldsTransitive(visiting, currentLibrary);
            toVisit.addAll(fields);
        }
    }

    private Set<CtClass> findSuperClassAndInterfacesTransitive(CtClass ctClass, DependencyTreeNode currentLibrary) {
        Set<CtClass> usedClasses = new HashSet<>();
        try {
            CtClass superClass = ctClass.getSuperclass();
            if (superClass != null) {
                Optional<CtClass> optional = computeUsedClassTransitive(superClass, currentLibrary);
                optional.ifPresent(usedClasses::add);
            }
        } catch (NotFoundException e) {
            LOG.warn("Superclass not found: {}", e.getMessage());
        }

        try {
            CtClass[] interfaces = ctClass.getInterfaces();
            for (CtClass interfaze : interfaces) {
                Optional<CtClass> optional = computeUsedClassTransitive(interfaze, currentLibrary);
                optional.ifPresent(usedClasses::add);
            }
        } catch (NotFoundException e) {
            LOG.warn("Interfaces not found: {}", e.getMessage());
        }

        return usedClasses;
    }

    private Set<CtClass> findFieldsTransitive(CtClass ctClass, DependencyTreeNode currentLibrary) {
        Set<CtClass> declaredClasses = new HashSet<>();
        CtField[] fields = ctClass.getDeclaredFields();
        for (CtField field : fields) {
            if (field.getGenericSignature() != null) {
                Set<CtClass> types = findTypesInGeneric(field); // It has generic type
                types.forEach(type -> {
                    Optional<CtClass> optional = computeUsedClassTransitive(type, currentLibrary);
                    optional.ifPresent(declaredClasses::add);
                });
            } else {
                Optional<CtClass> typeOptional = findTypeInSimpleField(field); // It is a simple type
                typeOptional.ifPresent(type -> {
                    Optional<CtClass> optional = computeUsedClassTransitive(type, currentLibrary);
                    optional.ifPresent(declaredClasses::add);
                });
            }
        }

        return declaredClasses;
    }

    private Optional<CtClass> computeUsedClassTransitive(CtClass usedClass, DependencyTreeNode currentLibrary) {
        try {
            // 1. From a standard library -> discard
            if (classPoolManager.isStandardClass(usedClass)) return Optional.empty();
            // 2. From a dependency -> add to reachableMethods of the dependency
            if (classPoolManager.isClassInDependency(usedClass, currentLibrary.getLibrary().getLibraryPath())) {
                addReachableClass(usedClass);
                return Optional.empty();
            }
            // 3. From the current library -> return to visit in the future and add to reachable classes
            currentLibrary.addReachableClass(usedClass);
            return Optional.of(usedClass);
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: {}", usedClass.getName());
            return Optional.empty();
        }
    }

    // SHARED IN DIRECT AND TRANSITIVE

    /**
     * Adds the type of a declared field as a reachable API Field Class of the library to which it is implemented.
     * @param ctClass
     * @param distance
     * @param declarations
     * @throws NotFoundException
     */
    private void addReachableFieldClass(CtClass ctClass, Integer distance, Set<CtField> declarations) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableApiFieldClass(distance, ctClass, declarations);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
    }

    /**
     * Adds the class as a reachable class of the library in which it is implemented.
     * @param ctClass
     * @throws NotFoundException
     */
    private void addReachableClass(CtClass ctClass) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = this.rootLibrary.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableClass(ctClass);
        } else {
            LOG.warn("Library not found in tree: {}", serverLibrary);
        }
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
            LOG.warn("Not able to find class of field: {}", field.getSignature());
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
