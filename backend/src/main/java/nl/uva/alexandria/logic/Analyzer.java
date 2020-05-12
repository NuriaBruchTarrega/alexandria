package nl.uva.alexandria.logic;

import nl.uva.alexandria.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public void analyze(String pathToClientLibraryJarFolder, String clientLibrary) {
        // Obtain Jar
        FileManager fileManager = new FileManager();
        String clientLibraryJar = fileManager.getClientLibraryPath(pathToClientLibraryJarFolder, clientLibrary);
    }
}
