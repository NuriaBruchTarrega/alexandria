package nl.uva.alexandria.logic.metrics;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import nl.uva.alexandria.model.ServerClass;
import nl.uva.alexandria.utils.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class AggregationCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);
    private final ClassPool pool;
    Map<ServerClass, Integer> declaredClasses;

    public AggregationCalculator(ClassPool pool) {
        this.pool = pool;
        this.declaredClasses = new HashMap<>();
    }

    public Map<String, Integer> calculateAggregationCoupling(Set<CtClass> clientClasses) {
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

        Map<String, Integer> acByLibrary = getACByLibrary(declaredClasses);
        return acByLibrary;
    }

    private void computeFieldWithGeneric(CtField field) {
        String gen = field.getGenericSignature();
        List<String> classNames = ClassNameUtils.getClassNamesFromGenericSignature(gen);
        List<CtClass> classes = new ArrayList<>();
        classNames.forEach(className -> {
            try {
                classes.add(pool.get(className));
            } catch (NotFoundException e) {
                LOG.warn("Class not found: " + className);
            }
        });

        classes.forEach(c -> computeClass(c));
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
        return pool.getCtClass(className);
    }

    private ServerClass createServerClass(CtClass type) throws NotFoundException {
        String className = type.getName();
        String library = ClassNameUtils.getLibraryName(type.getURL().getFile());
        return new ServerClass(library, className);
    }

    private Map<String, Integer> getACByLibrary(Map<ServerClass, Integer> declaredTypes) {
        Map<String, Integer> acByLibrary = new HashMap<>();

        declaredTypes.forEach((serverClass, numDec) -> {
            String library = serverClass.getLibrary();
            acByLibrary.computeIfPresent(library, (key, value) -> value + numDec);
            acByLibrary.putIfAbsent(library, numDec);
        });

        return acByLibrary;
    }
}
