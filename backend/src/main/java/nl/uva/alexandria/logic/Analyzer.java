package nl.uva.alexandria.logic;

import javassist.ClassPool;
import javassist.NotFoundException;
import nl.uva.alexandria.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public void analyze(String pathToClientLibraryJarFolder, String clientLibrary) {
        // Obtain client library Jar
        FileManager fileManager = new FileManager();
        String clientLibraryJar = fileManager.getClientLibraryJarPath(pathToClientLibraryJarFolder, clientLibrary);

        // Obtain all dependency jar files using maven invoker
        // TODO: discover how to use the maven invoker

        // Obtain all server libraries jar file names.
        List<String> serverLibrariesJars = fileManager.getServerLibrariesJarPaths(pathToClientLibraryJarFolder);
    }
}
