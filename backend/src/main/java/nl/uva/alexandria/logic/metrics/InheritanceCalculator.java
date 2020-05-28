package nl.uva.alexandria.logic.metrics;

import javassist.*;
import nl.uva.alexandria.logic.ClassPoolManager;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.ServerClass;
import nl.uva.alexandria.model.ServerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

public class InheritanceCalculator {

    private static final Logger LOG = LoggerFactory.getLogger(InheritanceCalculator.class);

    private final ClassPoolManager classPoolManager;
    private Map<ServerMethod, Integer> polymorphicImplementations;
    private Map<ServerClass, Integer> descendants;

    public InheritanceCalculator(ClassPoolManager classPoolManager) {
        this.classPoolManager = classPoolManager;
        this.polymorphicImplementations = new HashMap<>();
        this.descendants = new HashMap<>();
    }

    public void considerInheritance(Map<ServerMethod, Integer> micByClass, Map<ServerClass, Integer> acByClass) {
        Set<ServerMethod> serverMethodsSet = micByClass.keySet();
        Set<ServerClass> serverClassSet = acByClass.keySet();
        Map<Library, List<ServerMethod>> serverMethodByLibrary = serverMethodsSet.stream().collect(Collectors.groupingBy(m -> m.getLibrary()));
        Map<Library, List<ServerClass>> serverClassByLibrary = serverClassSet.stream().collect(Collectors.groupingBy(c -> c.getLibrary()));

        Set<Library> libraries = new HashSet<>();
        libraries.addAll(serverMethodByLibrary.keySet());
        libraries.addAll(serverClassByLibrary.keySet());

        libraries.forEach(library -> {
            try {
                List<ServerMethod> serverMethods = serverMethodByLibrary.get(library);
                if (serverMethods == null) serverMethods = new ArrayList<>();
                List<ServerClass> serverClasses = serverClassByLibrary.get(library);
                if (serverClasses == null) serverClasses = new ArrayList<>();
                calculateInheritance(library, serverMethods, serverClasses);
            } catch (NotFoundException e) {
                LOG.error("Library classes not found: {}", stackTraceToString(e));
            }
        });

        serverMethodsSet.forEach(serverMethod -> {
            Integer numPolymorphicImplementations = this.polymorphicImplementations.get(serverMethod);
            micByClass.compute(serverMethod, (key, value) -> value * numPolymorphicImplementations);
        });

        serverClassSet.forEach(serverClass -> {
            Integer numDescendants = this.descendants.get(serverClass);
            acByClass.compute(serverClass, (key, value) -> value * numDescendants);
        });
    }

    private void calculateInheritance(Library library, List<ServerMethod> serverMethodList, List<ServerClass> serverClassList) throws NotFoundException {
        polymorphicImplementations.putAll(serverMethodList.stream().collect(Collectors.toMap(serverMethod -> serverMethod, serverMethod -> 0)));
        descendants.putAll(serverClassList.stream().collect(Collectors.toMap(serverClass -> serverClass, serverClass -> 0)));
        Set<CtClass> libraryClasses = this.classPoolManager.getLibraryClasses(library.getLibraryPath());

        for (CtClass libraryClass : libraryClasses) {
            for (ServerMethod serverMethod : serverMethodList) {
                if (!libraryClass.subclassOf(serverMethod.getCtClass())) continue;
                try {
                    CtBehavior invokedMethod = serverMethod.getBehavior();

                    if (invokedMethod instanceof CtMethod) {
                        // If there is no exception, the method has been found
                        libraryClass.getDeclaredMethod(invokedMethod.getName(), invokedMethod.getParameterTypes());
                    } else if (invokedMethod instanceof CtConstructor) {
                        // If there is no exception, the constructor has been found
                        libraryClass.getConstructor(invokedMethod.getSignature());
                    } else continue; // In case at some point there is another type of behavior

                    polymorphicImplementations.computeIfPresent(serverMethod, (key, value) -> value + 1);
                } catch (NotFoundException e) {
                    // Class does not have polymorphic implementation of the method
                    continue;
                }
            }

            for (ServerClass serverClass : serverClassList) {
                if (!libraryClass.subclassOf(serverClass.getCtClass())) continue;
                descendants.computeIfPresent(serverClass, (key, value) -> value + 1);
            }
        }
    }
}
