package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.factories.LibraryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AggregationCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);

    private final ClassPoolManager classPoolManager;

    public AggregationCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public DependencyTreeNode calculateAggregationCoupling(DependencyTreeNode dependencyTreeNode) {
        // Calculate direct coupling
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        computeStableDeclaredFields(clientClasses, dependencyTreeNode);

        // Find descendants
        // Map<ServerClass, Integer> mapAcDescendants = DescendantsDetector.countDescendants(stableDeclaredFields, classPoolManager);

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
                    computeFieldWithGeneric(field, dependencyTreeNode); // It has generic type
                } else {
                    computeSimpleField(field, dependencyTreeNode); // It is a simple type
                }
            }
        });
    }

    // Methods to compute the different fields
    private void computeFieldWithGeneric(CtField field, DependencyTreeNode dependencyTreeNode) {
        String gen = field.getGenericSignature();
        List<String> classNames = ClassNameUtils.getClassNamesFromGenericSignature(gen);
        Set<CtClass> classes = classPoolManager.getClassesFromClassNames(classNames);
        classes.forEach(clazz -> computeField(clazz, dependencyTreeNode));
    }

    private void computeSimpleField(CtField field, DependencyTreeNode dependencyTreeNode) {
        try {
            CtClass serverClass = field.getType();
            if (serverClass.isPrimitive()) return; // Ignore primitives
            if (serverClass.isArray()) {
                Optional<CtClass> arrayType = getTypeOfArray(field);
                if (arrayType.isEmpty() || arrayType.get().isPrimitive()) return;
                computeField(arrayType.get(), dependencyTreeNode);
            } else {
                computeField(serverClass, dependencyTreeNode);
            }
        } catch (NotFoundException e) {
            LOG.warn("Not able to find class of field: " + field.getSignature());
        }
    }

    private void computeField(CtClass clazz, DependencyTreeNode dependencyTreeNode) {
        try {
            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInDependency(clazz)) {
                addReachableField(clazz, dependencyTreeNode, 1, 1);
            }
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: " + clazz.getName());
        }

    }

    // SHARED IN DIRECT AND TRANSITIVE
    private void addReachableField(CtClass ctClass, DependencyTreeNode dependencyTreeNode, Integer distance, Integer numAffectedLines) throws NotFoundException {
        Library serverLibrary = LibraryFactory.getLibraryFromClassPath(ctClass.getURL().getPath());
        Optional<DependencyTreeNode> libraryNode = dependencyTreeNode.findLibraryNode(serverLibrary);
        if (libraryNode.isPresent()) {
            libraryNode.get().addReachableApiField(distance, ctClass, numAffectedLines);
        } else LOG.warn("Library not found in tree: {}", serverLibrary.toString());
    }

    private Optional<CtClass> getTypeOfArray(CtField field) throws NotFoundException {
        String signature = field.getSignature();
        String className = ClassNameUtils.signatureToClassName(signature);

        if (className.length() == 0) return Optional.empty();
        return Optional.ofNullable(classPoolManager.getClassFromClassName(className));
    }
}
