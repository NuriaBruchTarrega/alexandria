package nl.uva.alexandria.logic;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.metrics.AggregationCalculator;
import nl.uva.alexandria.logic.metrics.MethodInvocationsCalculator;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import nl.uva.alexandria.utils.ClassNameUtils;
import nl.uva.alexandria.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    private final Parser parser;

    public Analyzer(Parser parser) {
        this.parser = parser;
    }

    public AnalysisResponse analyze(String pathToClientLibraryJarFolder, String clientLibrary) {

        // Obtain client library Jar
        String clientLibraryJar = FileManager.getClientLibraryJarPath(pathToClientLibraryJarFolder, clientLibrary);

        // Obtain all dependency jar files using maven invoker
        try {
            downloadDependencies(pathToClientLibraryJarFolder);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Unable to retrieve dependencies");
            return null;
        }

        // Obtain all server libraries jar file names.
        List<String> serverLibrariesJars = FileManager.getServerLibrariesJarPaths(pathToClientLibraryJarFolder);
        List<String> serverLibrariesNames = serverLibrariesJars.stream().map(ClassNameUtils::getLibraryName).collect(Collectors.toList());

        // Create class pool with client and servers
        ClassPoolManager classPoolManager = new ClassPoolManager();
        try {
            classPoolManager.createClassPool(clientLibraryJar, serverLibrariesJars);
        } catch (NotFoundException e) {
            LOG.error("Error creating class pool");
            return null;
        }

        // Obtain classes from clientLibrary
        List<String> clientClassesNames = getClientClassesNames(clientLibraryJar);
        Set<CtClass> clientClasses;
        try {
            clientClasses = classPoolManager.getClientClasses(clientClassesNames);
        } catch (NotFoundException e) {
            LOG.error("Error obtaining the client classes CtClass");
            e.printStackTrace();
            return null;
        }

        // Calculate metrics
        Map<String, Integer> mapMIC = new HashMap<>();
        Map<String, Integer> mapAC = new HashMap<>();
        serverLibrariesNames.forEach(serverLibraryName -> mapMIC.putIfAbsent(serverLibraryName, 0));
        serverLibrariesNames.forEach(serverLibraryName -> mapAC.putIfAbsent(serverLibraryName, 0));

        MethodInvocationsCalculator miCalculator = new MethodInvocationsCalculator(mapMIC);
        AggregationCalculator aggregationCalculator = new AggregationCalculator(mapAC, classPoolManager);

        miCalculator.calculateMethodInvocations(clientClasses);
        aggregationCalculator.calculateAggregationCoupling(clientClasses);

        System.out.println("DONE\n");
        System.out.println("MIC: " + mapMIC.toString());
        System.out.println("AC: " + mapAC.toString());

        return new AnalysisResponse(mapMIC, mapAC);
    }

    private List<String> getClientClassesNames(String clientLibraryJar) {
        List<String> classFiles = FileManager.getClassFiles(clientLibraryJar);
        return classFiles.stream().map(ClassNameUtils::getFullyQualifiedName).collect(Collectors.toList());
    }

    private void downloadDependencies(String pathToClientLibraryJarFolder) throws IOException {
        // To download pom files add: -Dmdep.copyPom=true
        // mvn.cmd -f pathToPom dependency:copy-dependencies
        String[] consoleLog = MavenInvoker.runCommand("mvn.cmd -f " + pathToClientLibraryJarFolder + " dependency:copy-dependencies");
        if (Arrays.stream(consoleLog).anyMatch(log -> log.startsWith("[ERROR]") || log.startsWith("[FATAL]")))
            throw new IOException();
    }
}
