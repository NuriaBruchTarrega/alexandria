package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.ServerClass;
import nl.uva.alexandria.utils.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregationCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);
    private final ClassPoolManager classPoolManager;
    private final Map<String, Integer> ac;
    private Map<ServerClass, Integer> declaredClasses;

    public AggregationCalculator(Map<String, Integer> ac, ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
        this.declaredClasses = new HashMap<>();
        this.ac = ac;
    }

    public void calculateAggregationCoupling(Set<CtClass> clientClasses) {
        // Loop through all the classes
        clientClasses.forEach(clientClass -> {
            // Get all fields
            CtField[] fields = clientClass.getDeclaredFields();
            // Get all stable types
            for (CtField field : fields) {
                if (field.getGenericSignature() != null) {
                    computeFieldWithGeneric(field); // It has generic type
                } else {
                    computeField(field); // It is a simple type
                }
            }
        });
        updateACByLibrary(declaredClasses);
        //return getACByLibrary(declaredClasses);
    }

    private void computeFieldWithGeneric(CtField field) {
        String gen = field.getGenericSignature();
        List<String> classNames = ClassNameUtils.getClassNamesFromGenericSignature(gen);
        Set<CtClass> classes = classPoolManager.getClassesFromClassNames(classNames);
        classes.forEach(this::computeClass);
    }

    private void computeField(CtField field) {
        try {
            CtClass serverClass = field.getType();
            if (serverClass.isPrimitive()) return; // Ignore primitives
            if (serverClass.isArray()) {
                serverClass = obtainTypeInArray(field);
                if (serverClass == null || serverClass.isPrimitive()) return;
            }

            computeClass(serverClass);
        } catch (NotFoundException e) {
            LOG.warn("Not able to find class of field: " + field.getSignature());
        }
    }

    private void computeClass(CtClass serverClass) {
        try {
            URL url = serverClass.getURL();
            // Filter out everything that is not in the server libraries
            if (url.getProtocol().equals("jar") && url.getPath().contains("target/dependency")) {
                ServerClass sc = createServerClass(serverClass);
                declaredClasses.computeIfPresent(sc, (key, value) -> value + 1);
                declaredClasses.putIfAbsent(sc, 1);
            }
        } catch (NotFoundException e) {
            LOG.warn("Not found URL of class: " + serverClass.getName());
        }

    }

    private CtClass obtainTypeInArray(CtField field) throws NotFoundException {
        String signature = field.getSignature();
        String className = ClassNameUtils.signatureToClassName(signature);

        if (className.length() == 0) return null;
        return classPoolManager.getClassFromClassName(className);
    }

    private ServerClass createServerClass(CtClass type) throws NotFoundException {
        String className = type.getName();
        String library = ClassNameUtils.getLibraryName(type.getURL().getFile());
        return new ServerClass(library, className);
    }

    private void updateACByLibrary(Map<ServerClass, Integer> declaredTypes) {
        declaredTypes.forEach((serverClass, numDec) -> {
            String library = serverClass.getLibrary();
            ac.computeIfPresent(library, (key, value) -> value + numDec);
            ac.putIfAbsent(library, numDec);
        });
    }
}
