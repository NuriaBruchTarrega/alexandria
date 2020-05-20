package nl.uva.alexandria.logic;

import javassist.CtClass;
import javassist.NotFoundException;
import nl.uva.alexandria.logic.metrics.AggregationCalculator;
import nl.uva.alexandria.logic.metrics.MethodInvocationsCalculator;
import nl.uva.alexandria.logic.utils.ClassNameUtils;
import nl.uva.alexandria.logic.utils.FileManager;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import nl.uva.alexandria.model.factories.LibraryFactory;
import org.eclipse.aether.collection.DependencyCollectionException;
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

    public AnalysisResponse analyze(String groupID, String artifactID, String version) {

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
        List<Dependency> dependencies;
        List<ArtifactDescriptorResult> serverLibrariesDescriptors;
        try {
            dependencies = artifactManager.getDependencies(artifactDescriptor);
            serverLibrariesDescriptors = artifactManager.getDependenciesDescriptors(dependencies);
        } catch (DependencyCollectionException e) {
            e.printStackTrace();
            LOG.error("Unable to collect dependencies");
            return null;
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
        Map<Library, Integer> mapMIC = new HashMap<>();
        Map<Library, Integer> mapAC = new HashMap<>();
        List<String> directDependenciesGAV = artifactManager.getDirectDependencies();
        List<Library> directDependencies = directDependenciesGAV.stream().map(LibraryFactory::getLibraryFromGAV).collect(Collectors.toList());

        directDependencies.forEach(directDependency -> mapMIC.putIfAbsent(directDependency, 0));
        directDependencies.forEach(directDependency -> mapAC.putIfAbsent(directDependency, 0));

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
