package nl.uva.alexandria.logic;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.logic.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPoolManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClassPoolManager.class);
    private static final String JAR_PROTOCOL = "jar";

    private ClassPool classPool;
    private File clientLibraryJarFile;
    private Set<CtClass> clientClasses;

    ClassPoolManager(File clientLibraryJar, List<File> serverLibrariesJars) throws NotFoundException {
        try {
            this.classPool = ClassPool.getDefault();
            this.clientLibraryJarFile = clientLibraryJar;
            URLClassLoader classLoader = (URLClassLoader) this.getClass().getClassLoader();
            // TODO: find a better solution to do this
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);

            // Add clientLibrary to ClassPool
            classPool.insertClassPath(clientLibraryJar.getAbsolutePath());
            method.invoke(classLoader, clientLibraryJar.toURI().toURL());


            // Add server libraries to ClassPool
            for (File serverLibraryJar : serverLibrariesJars) {
                classPool.insertClassPath(serverLibraryJar.getAbsolutePath());
                method.invoke(classLoader, serverLibraryJar.toURI().toURL());
            }

            this.clientClasses = getClassesFromLibrary(getClientClassesNames(clientLibraryJarFile.getAbsolutePath()));

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public Set<CtClass> getClientClasses() {
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
                LOG.warn("Class not found: {}", className);
            }
        });

        return classes;
    }

    public boolean isClassInDependency(CtClass clazz) throws NotFoundException {
        URL url = clazz.getURL();
        return url.getProtocol().equals(JAR_PROTOCOL) && !url.getPath().contains(clientLibraryJarFile.getName());
    }

    public boolean isClassInDependency(CtClass clazz, String libraryPath) throws NotFoundException {
        URL url = clazz.getURL();
        return url.getProtocol().equals(JAR_PROTOCOL) && !url.getPath().replace("/", File.separator).contains(libraryPath);
    }

    public boolean isStandardClass(CtClass clazz) throws NotFoundException {
        URL url = clazz.getURL();
        return !url.getProtocol().equals(JAR_PROTOCOL);
    }

    public Set<CtClass> getLibraryClasses(String libraryJarPath) throws NotFoundException {
        List<String> libraryClassPaths = FileManager.getClassFiles(libraryJarPath);
        List<String> libraryClassNames = libraryClassPaths.stream().map(ClassNameUtils::getFullyQualifiedNameFromClassPath).collect(Collectors.toList());
        return getClassesFromLibrary(libraryClassNames);
    }

    private Set<CtClass> getClassesFromLibrary(List<String> clientClassesNames) throws NotFoundException {
        Set<CtClass> clientCtClasses = new HashSet<>();

        for (String className : clientClassesNames) {
            CtClass clazz = classPool.get(className);
            clientCtClasses.add(clazz);
        }

        return clientCtClasses;
    }

    private List<String> getClientClassesNames(String clientLibraryJar) {
        List<String> classFiles = FileManager.getClassFiles(clientLibraryJar);
        return classFiles.stream().map(ClassNameUtils::getFullyQualifiedNameFromClassPath).collect(Collectors.toList());
    }
}
