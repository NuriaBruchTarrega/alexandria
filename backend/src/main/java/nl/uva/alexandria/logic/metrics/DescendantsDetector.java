package nl.uva.alexandria.logic.metrics;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

class DescendantsDetector {

    private static final Logger LOG = LoggerFactory.getLogger(DescendantsDetector.class);

    private DescendantsDetector() {
    }

    static int numDescendants(ServerClass sc, ClassPoolManager cpm) throws NotFoundException {
        String libraryJarPath = sc.getLibrary().getLibraryPath();
        Set<CtClass> libraryClasses = cpm.getLibraryClasses(libraryJarPath);
        List<CtClass> descendants = findDescendants(sc, libraryClasses);

        return descendants.size();
    }

    private static List<CtClass> findDescendants(ServerClass sc, Set<CtClass> libraryClasses) {
        List<CtClass> descendants = new ArrayList<>();

        CtClass serverClass = sc.getCtClass();

        for (CtClass libraryClass : libraryClasses) {
            if (!libraryClass.subclassOf(serverClass)) continue;
            descendants.add(libraryClass);
        }

        return descendants;
    }

    public static Map<ServerClass, Integer> improvedDescendants(Map<ServerClass, Integer> stableDeclaredFields, ClassPoolManager classPoolManager) {
        Set<ServerClass> serverClasses = stableDeclaredFields.keySet();
        Map<ServerClass, Integer> mapDescendants = new HashMap<>();
        Map<Library, List<ServerClass>> mapLibraryServerClass = serverClasses.stream().collect(Collectors.groupingBy(m -> m.getLibrary()));

        mapLibraryServerClass.forEach(((library, serverClassList) -> {
            try {
                Map<ServerClass, Integer> mapLibraryDescendants = calculateDescendants(library, serverClassList, classPoolManager);
                mapDescendants.putAll(mapLibraryDescendants);
            } catch (NotFoundException e) {
                LOG.error("Library classes not found: {}", stackTraceToString(e));
            }
        }));

        serverClasses.forEach(serverClass -> {
            Integer numDescendants = mapDescendants.get(serverClass);
            stableDeclaredFields.compute(serverClass, (key, value) -> value * numDescendants);
        });

        return stableDeclaredFields;
    }

    private static Map<ServerClass, Integer> calculateDescendants(Library library, List<ServerClass> serverClassList, ClassPoolManager classPoolManager) throws NotFoundException {
        Map<ServerClass, Integer> mapDescendants = serverClassList.stream().collect(Collectors.toMap(serverClass -> serverClass, serverClass -> 0));
        Set<CtClass> libraryClasses = classPoolManager.getLibraryClasses(library.getLibraryPath());

        for (CtClass libraryClass : libraryClasses) {
            for (ServerClass serverClass : serverClassList) {

                if (!libraryClass.subclassOf(serverClass.getCtClass())) continue;
                mapDescendants.computeIfPresent(serverClass, (key, value) -> value + 1);
            }
        }

        return mapDescendants;
    }
}
