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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AggregationCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(AggregationCalculator.class);
    private final ClassPool pool;

    public AggregationCalculator(ClassPool pool) {
        this.pool = pool;
    }

    public Map<String, Integer> calculateAggregationCoupling(Set<CtClass> clientClasses) {
        Map<ServerClass, Integer> declaredClasses = new HashMap<>();

        // Loop through all the classes
        clientClasses.forEach(clientClass -> {
            // Get all fields
            CtField[] fields = clientClass.getDeclaredFields();
            // Get all stable types
            for (CtField field : fields) {
                try {
                    CtClass type = field.getType();
                    if (type.isPrimitive()) continue; // Ignore primitives
                    if (type.isArray()) {
                        type = obtainTypeInArray(field);
                        if (type == null || type.isPrimitive()) continue;
                    }

                    URL url = type.getURL();

                    // Filter out everything that is not in the server libraries
                    if (url.getProtocol().equals("jar") && url.getPath().contains("target/dependency")) {
                        ServerClass sc = createServerClass(type);
                        declaredClasses.computeIfPresent(sc, (key, value) -> value + 1);
                        declaredClasses.putIfAbsent(sc, 1);
                    }

                } catch (NotFoundException e) {
                    LOG.warn("Type not found for field: " + field.getName());
                }
            }
        });

        Map<String, Integer> acByLibrary = getACByLibrary(declaredClasses);

        return acByLibrary;
    }

    private CtClass obtainTypeInArray(CtField field) throws NotFoundException {
        String signature = field.getSignature();
        String className = ClassNameUtils.arraySignatureToClassName(signature);

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
