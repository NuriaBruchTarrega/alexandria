package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;
import nl.uva.alexandria.model.factories.ServerClassFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregationCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);

    private final ClassPoolManager classPoolManager;
    private Map<ServerClass, Integer> stableDeclaredFields;
    private Map<Library, Integer> mapAC;

    public AggregationCalculator(Map<Library, Integer> mapAC, ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
        this.stableDeclaredFields = new HashMap<>();
        this.mapAC = mapAC;
    }

    public void calculateAggregationCoupling(Set<CtClass> clientClasses) {
        // Loop through all the classes to find stable fields declared in them
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

        // Calculate AC for each library
        updateMapAC(mapACDescendants);
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
                serverClass = getTypeOfArray(field);
                if (serverClass == null || serverClass.isPrimitive()) return;
            }

            computeClass(serverClass);
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

    private CtClass getTypeOfArray(CtField field) throws NotFoundException {
        String signature = field.getSignature();
        String className = ClassNameUtils.signatureToClassName(signature);

        if (className.length() == 0) return null;
        return classPoolManager.getClassFromClassName(className);
    }

    private void updateMapAC(Map<ServerClass, Integer> mapACDescendants) {
        mapACDescendants.forEach((serverClass, numDec) -> {
            Library library = serverClass.getLibrary();
            mapAC.computeIfPresent(library, (key, value) -> value + numDec);
            mapAC.putIfAbsent(library, numDec);
        });
    }
}
