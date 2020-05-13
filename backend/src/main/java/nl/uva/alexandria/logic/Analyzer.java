package nl.uva.alexandria.logic;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.metrics.MethodInvocationsCalculator;
import nl.uva.alexandria.utils.ClassNameUtils;
import nl.uva.alexandria.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public void analyze(String pathToClientLibraryJarFolder, String clientLibrary) {
        MethodInvocationsCalculator miCalculator = new MethodInvocationsCalculator();

        // Obtain client library Jar
        String clientLibraryJar = FileManager.getClientLibraryJarPath(pathToClientLibraryJarFolder, clientLibrary);

        // Obtain all dependency jar files using maven invoker
        try {
            downloadDependencies(pathToClientLibraryJarFolder);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Unable to retrieve dependencies");
            return;
        }

        // Obtain all server libraries jar file names.
        List<String> serverLibrariesJars = FileManager.getServerLibrariesJarPaths(pathToClientLibraryJarFolder);

        // Create class pool with client and servers
        ClassPool pool = createClassPool(clientLibraryJar, serverLibrariesJars);

        // Obtain classes from clientLibrary
        List<String> clientClassesNames = getClientClassesNames(clientLibraryJar);
        Set<CtClass> clientClasses = getClientClasses(clientClassesNames, pool);

        // Calculate MIC
        Map<String, Integer> mic = miCalculator.calculateMethodInvocations(clientClasses);

        System.out.println("DONE\n");
        System.out.println(mic.toString());
    }

    private Set<CtClass> getClientClasses(List<String> clientClassesNames, ClassPool pool) {
        Set<CtClass> clientClasses = new HashSet<>();
        clientClassesNames.forEach(className -> {
            try {
                clientClasses.add(pool.get(className));
            } catch (NotFoundException e) {
                LOG.warn("Class not found" + className);
            }
        });

        return clientClasses;
    }

    private void downloadDependencies(String pathToClientLibraryJarFolder) throws IOException {
        // To download pom files add: -Dmdep.copyPom=true
        // mvn.cmd -f pathToPom dependency:copy-dependencies
        MavenInvoker.runCommand("mvn.cmd -f " + pathToClientLibraryJarFolder + " dependency:copy-dependencies");
    }

    private ClassPool createClassPool(String clientLibraryJar, List<String> serverLibrariesJars) {
        ClassPool pool = ClassPool.getDefault();

        try {
            pool.insertClassPath(clientLibraryJar); // Add clientLibrary to ClassPool
        } catch (NotFoundException e) {
            LOG.warn("Error inserting client library jar");
        }

        serverLibrariesJars.forEach(serverLibraryJar -> { // Add server libraries to ClassPool
            try {
                pool.insertClassPath(serverLibraryJar);
            } catch (NotFoundException e) {
                LOG.warn("Error inserting server library {}", serverLibraryJar);
            }
        });

        return pool;
    }

    private List<String> getClientClassesNames(String clientLibraryJar) {
        List<String> classFiles = FileManager.getClassFiles(clientLibraryJar);
        return classFiles.stream().map(ClassNameUtils::getFullyQualifiedName).collect(Collectors.toList());
    }
}
