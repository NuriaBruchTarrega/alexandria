package nl.uva.alexandria.logic;

import javassist.ClassPool;
import javassist.NotFoundException;
import nl.uva.alexandria.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public void analyze(String pathToClientLibraryJarFolder, String clientLibrary) {
        // Obtain client library Jar
        String clientLibraryJar = FileManager.getClientLibraryJarPath(pathToClientLibraryJarFolder, clientLibrary);

        // Obtain all dependency jar files using maven invoker
        // TODO: discover how to use the maven invoker

        // Obtain all server libraries jar file names.
        List<String> serverLibrariesJars = FileManager.getServerLibrariesJarPaths(pathToClientLibraryJarFolder);

        // Create class pool
        ClassPool pool = createClassPool(clientLibraryJar, serverLibrariesJars);

        // Obtain fully qualified name of classes from clientLibrary
        List<String> classes = getClientClasses(clientLibraryJar);
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

    private List<String> getClientClasses(String clientLibraryJar) {
        List<String> classFiles = FileManager.getClassFiles(clientLibraryJar);
        return classFiles.stream().map(file -> getFullyQualifiedName(file)).collect(Collectors.toList());
    }

    private String getFullyQualifiedName(String classPath) {
        return classPath.replace(".class", "").replace("/", ".");
    }
}
