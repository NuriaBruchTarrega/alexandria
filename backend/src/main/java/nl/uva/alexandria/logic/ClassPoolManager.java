package nl.uva.alexandria.logic;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassPoolManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClassPoolManager.class);
    private static final String JAR_PROTOCOL = "jar";
    private static final String DEPENDENCY_FOLDER = "target/dependency";

    private ClassPool classPool;
    private String clientLibraryJar;

    public void createClassPool(String clientLibraryJar, List<String> serverLibrariesJars) throws NotFoundException {
        this.classPool = ClassPool.getDefault();
        this.clientLibraryJar = clientLibraryJar;

        // Add clientLibrary to ClassPool
        classPool.insertClassPath(clientLibraryJar);

        // Add server libraries to ClassPool
        for (String serverLibraryJar : serverLibrariesJars) {
            classPool.insertClassPath(serverLibraryJar);
        }
    }

    public Set<CtClass> getClientClasses(List<String> clientClassesNames) throws NotFoundException {
        Set<CtClass> clientClasses = new HashSet<>();

        for (String className : clientClassesNames) {
            CtClass clazz = classPool.get(className);
            if (!clazz.isEnum()) clientClasses.add(clazz); // Discard enums
        }

        return clientClasses;
    }

    public CtClass getClassFromClassName(String className) throws NotFoundException {
        return classPool.get(className);
    }

    public Set<CtClass> getClassesFromClassNames(List<String> classNames) {
        Set<CtClass> classes = new HashSet<>();

        classNames.forEach(className -> {
            try {
                classes.add(getClassFromClassName(className));
            } catch (NotFoundException e) {
                LOG.warn("Class not found: " + className);
            }
        });

        return classes;
    }

    public boolean isClassInServerLibrary(CtClass clazz) throws NotFoundException {
        URL url = clazz.getURL();
        return url.getProtocol().equals(JAR_PROTOCOL) && url.getPath().contains(DEPENDENCY_FOLDER);
    }
}
