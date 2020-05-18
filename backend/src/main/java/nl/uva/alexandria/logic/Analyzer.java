package nl.uva.alexandria.logic;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.metrics.AggregationCalculator;
import nl.uva.alexandria.logic.metrics.MethodInvocationsCalculator;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.logic.utils.FileManager;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Analyzer {

    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    public AnalysisResponse analyze(String pathToClientLibraryJarFolder, String clientLibrary, String groupID, String artifactID, String version) {

        // Download artifact from Maven Central. Descriptor and jar.
        ArtifactManager artifactManager = new ArtifactManager();
        ArtifactDescriptorResult artifactDescriptor;
        try {
            artifactDescriptor = artifactManager.getArtifactDescriptor(groupID, artifactID, version);
        } catch (ArtifactDescriptorException | ArtifactResolutionException e) {
            e.printStackTrace();
            LOG.error("Unable to retrieve artifact");
            return null;
        }
        File clientLibraryJarFile = artifactManager.getArtifactFile(artifactDescriptor);


        // Obtain all dependencies from artifact. Descriptor and jar.
        List<Dependency> dependencies = artifactManager.getDependencies(artifactDescriptor);
        List<ArtifactDescriptorResult> serverLibrariesDescriptors;
        try {
            serverLibrariesDescriptors = artifactManager.getDependenciesDescriptors(dependencies);
        } catch (ArtifactDescriptorException | ArtifactResolutionException e) {
            e.printStackTrace();
            LOG.error("Unable to retrieve dependencies artifacts");
            return null;
        }
        List<File> serverLibrariesJarFiles = artifactManager.getArtifactsFiles(serverLibrariesDescriptors);

        // Create class pool with client and servers
        ClassPoolManager classPoolManager = new ClassPoolManager();
        try {
            classPoolManager.createClassPool(clientLibraryJarFile, serverLibrariesJarFiles);
        } catch (NotFoundException e) {
            LOG.error("Error creating class pool");
            return null;
        }

        // Obtain classes from clientLibrary
        List<String> clientClassesNames = getClientClassesNames(clientLibraryJarFile.getAbsolutePath());
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
//        serverLibrariesNames.forEach(serverLibraryName -> mapMIC.putIfAbsent(serverLibraryName, 0));
//        serverLibrariesNames.forEach(serverLibraryName -> mapAC.putIfAbsent(serverLibraryName, 0));

        MethodInvocationsCalculator miCalculator = new MethodInvocationsCalculator(mapMIC, classPoolManager);
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
        return classFiles.stream().map(ClassNameUtils::getFullyQualifiedNameFromClassPath).collect(Collectors.toList());
    }
}
