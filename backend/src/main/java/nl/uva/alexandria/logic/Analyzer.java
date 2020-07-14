package nl.uva.alexandria.logic;

import javassist.NotFoundException;
import nl.uva.alexandria.logic.metrics.AggregationCalculator;
import nl.uva.alexandria.logic.metrics.Aggregator;
import nl.uva.alexandria.logic.metrics.MethodInvocationsCalculator;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.DependencyTreeResult;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nl.uva.alexandria.logic.utils.GeneralUtils.stackTraceToString;

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
            LOG.error("Unable to retrieve artifact\n\n{}", stackTraceToString(e));
            return null;
        }
        File clientLibraryJarFile = artifactManager.getArtifactFile(artifactDescriptor);


        // Obtain all dependencies from artifact. Descriptor and jar.
        List<File> serverLibrariesJarFiles;
        try {
            serverLibrariesJarFiles = getServerLibrariesJarFiles(artifactManager, artifactDescriptor);
        } catch (DependencyCollectionException e) {
            LOG.error("Unable to collect dependencies\n\n{}", stackTraceToString(e));
            return null;
        } catch (ArtifactDescriptorException | ArtifactResolutionException e) {
            LOG.error("Unable to retrieve dependencies artifacts\n\n{}", stackTraceToString(e));
            return null;
        }

        // Create class pool with client and servers
        ClassPoolManager classPoolManager;
        try {
            classPoolManager = new ClassPoolManager(clientLibraryJarFile, serverLibrariesJarFiles);
        } catch (NotFoundException e) {
            LOG.error("Error creating class pool\n\n{}", stackTraceToString(e));
            return null;
        }

        // Calculate metrics
        return calculateMetrics(artifactManager, classPoolManager);
    }

    private AnalysisResponse calculateMetrics(ArtifactManager artifactManager, ClassPoolManager classPoolManager) {
        MethodInvocationsCalculator miCalculator = new MethodInvocationsCalculator(classPoolManager);
        AggregationCalculator aggCalculator = new AggregationCalculator(classPoolManager);

        DependencyTreeNode dependencyTreeNode = artifactManager.generateCustomDependencyTree();
        DependencyTreeNode dependencyTreeWithMethodInvocations = miCalculator.calculateMethodInvocations(dependencyTreeNode);
        DependencyTreeNode dependencyTreeWithBothMetrics = aggCalculator.calculateAggregationCoupling(dependencyTreeWithMethodInvocations);

        // Aggregate metrics to library aggregation level
        DependencyTreeResult dependencyTreeResult = Aggregator.calculateResultTree(dependencyTreeWithBothMetrics);

        return new AnalysisResponse(dependencyTreeResult);
    }

    private List<File> getServerLibrariesJarFiles(ArtifactManager artifactManager, ArtifactDescriptorResult artifactDescriptor) throws DependencyCollectionException, ArtifactDescriptorException, ArtifactResolutionException {
        List<Dependency> dependencies = artifactManager.getDependencies(artifactDescriptor);
        List<ArtifactDescriptorResult> serverLibrariesDescriptors = artifactManager.getDependenciesDescriptors(dependencies);
        return artifactManager.getArtifactsFiles(serverLibrariesDescriptors);
    }

    private Map<Library, Integer> createMapWithDirectDependencies(ArtifactManager artifactManager) {
        List<String> directDependenciesGAV = artifactManager.getDirectDependencies();
        List<Library> directDependencies = directDependenciesGAV.stream().map(LibraryFactory::getLibraryFromGAV).collect(Collectors.toList());

        return directDependencies.stream().collect(Collectors.toMap(dd -> dd, dd -> 0));
    }

    private Map<Library, Integer> createMapWithAllDependencies(ArtifactManager artifactManager) {
        List<String> dependenciesGAV = artifactManager.getAllDependencies();
        List<Library> dependencies = dependenciesGAV.stream().map(LibraryFactory::getLibraryFromGAV).collect(Collectors.toList());

        return dependencies.stream().collect(Collectors.toMap(dependency -> dependency, dependency -> 0));
    }
}
