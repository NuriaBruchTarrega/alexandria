package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.ServerClass;
import nl.uva.alexandria.model.factories.ServerClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AggregationCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);

    private final ClassPoolManager classPoolManager;
    private final Map<ServerClass, Integer> stableDeclaredFields = new HashMap<>();

    public AggregationCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
    }

    public Map<ServerClass, Integer> calculateAggregationCoupling() {
        // Loop through all the classes to find stable fields declared in them
        Set<CtClass> clientClasses = classPoolManager.getClientClasses();
        computeStableDeclaredFields(clientClasses);

        // Find descendants
        Map<ServerClass, Integer> mapACDescendants = new HashMap<>();
        this.stableDeclaredFields.forEach((serverClass, numDeclarations) -> {
            try {
                Integer numDescendants = DescendantsDetector.numDescendants(serverClass, classPoolManager);
                mapACDescendants.put(serverClass, numDeclarations * numDescendants);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        });

        return mapACDescendants;
    }

    private void computeStableDeclaredFields(Set<CtClass> clientClasses) {
        clientClasses.forEach(clientClass -> {
            // Get all fields
            CtField[] fields = clientClass.getDeclaredFields();
            // Get all stable types
            for (CtField field : fields) {
                if (field.getGenericSignature() != null) {
                    computeFieldWithGeneric(field); // It has generic type
                } else {
                    computeSimpleField(field); // It is a simple type
                }
            }
        });
    }

    private void computeFieldWithGeneric(CtField field) {
        String gen = field.getGenericSignature();
        List<String> classNames = ClassNameUtils.getClassNamesFromGenericSignature(gen);
        Set<CtClass> classes = classPoolManager.getClassesFromClassNames(classNames);
        classes.forEach(this::computeClass);
    }

    private void computeSimpleField(CtField field) {
        try {
            CtClass serverClass = field.getType();
            if (serverClass.isPrimitive()) return; // Ignore primitives
            if (serverClass.isArray()) {
                Optional<CtClass> arrayType = getTypeOfArray(field);
                if (arrayType.isEmpty() || arrayType.get().isPrimitive()) return;
                computeClass(arrayType.get());
            } else {
                computeClass(serverClass);
            }
        } catch (NotFoundException e) {
            LOG.warn("Not able to find class of field: " + field.getSignature());
        }
    }

    private void computeClass(CtClass serverClass) {
        try {
            // Filter out everything that is not in the server libraries
            if (classPoolManager.isClassInServerLibrary(serverClass)) {
                String path = serverClass.getURL().getPath();
                ServerClass sc = ServerClassFactory.getServerClassFromCtClass(serverClass, path);
                stableDeclaredFields.computeIfPresent(sc, (key, value) -> value + 1);
                stableDeclaredFields.putIfAbsent(sc, 1);
            }
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: " + serverClass.getName());
        }

    }

    private Optional<CtClass> getTypeOfArray(CtField field) throws NotFoundException {
        String signature = field.getSignature();
        String className = ClassNameUtils.signatureToClassName(signature);

        if (className.length() == 0) return Optional.empty();
        return Optional.ofNullable(classPoolManager.getClassFromClassName(className));
    }
}
