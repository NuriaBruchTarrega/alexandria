package nl.uva.alexandria.logic;

import javassist.NotFoundException;
import nl.uva.alexandria.logic.exceptions.ArtifactNotFoundException;
import nl.uva.alexandria.logic.exceptions.ClassPoolException;
import nl.uva.alexandria.logic.metrics.Aggregator;
import nl.uva.alexandria.logic.metrics.DependencyTreeTraverser;
import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.DependencyTreeResult;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
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
import java.util.NoSuchElementException;

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
            throw new ArtifactNotFoundException("Unable to retrieve client library artifact");
        }
        File clientLibraryJarFile = artifactManager.getArtifactFile(artifactDescriptor);


        // Obtain all dependencies from artifact. Descriptor and jar.
        List<File> serverLibrariesJarFiles;
        try {
            serverLibrariesJarFiles = getServerLibrariesJarFiles(artifactManager, artifactDescriptor);
        } catch (DependencyCollectionException e) {
            // Throw exception 404 Not Found
            LOG.error("Unable to collect dependencies\n\n{}", stackTraceToString(e));
            throw new ArtifactNotFoundException("Unable to collect dependencies");
        } catch (ArtifactDescriptorException | ArtifactResolutionException e) {
            // Throw exception 404 Not Found
            LOG.error("Unable to retrieve dependencies artifacts\n\n{}", stackTraceToString(e));
            throw new NoSuchElementException("Unable to retrieve dependencies artifacts");
        }

        // Create class pool with client and servers
        ClassPoolManager classPoolManager;
        try {
            classPoolManager = new ClassPoolManager(clientLibraryJarFile, serverLibrariesJarFiles);
        } catch (NotFoundException e) {
            // Throw exception 500 Internal Error
            LOG.error("Error creating class pool\n\n{}", stackTraceToString(e));
            throw new ClassPoolException("Error creating class pool");
        }

        // Calculate metrics
        return calculateMetrics(artifactManager, classPoolManager);
    }

    private AnalysisResponse calculateMetrics(ArtifactManager artifactManager, ClassPoolManager classPoolManager) {
        DependencyTreeTraverser dependencyTreeTraverser = new DependencyTreeTraverser(classPoolManager);

        DependencyTreeNode dependencyTreeNode = artifactManager.generateCustomDependencyTree();
        dependencyTreeTraverser.traverseTree(dependencyTreeNode);

        // Aggregate metrics to library aggregation level
        DependencyTreeResult dependencyTreeResult = Aggregator.calculateResultTree(dependencyTreeNode);

        return new AnalysisResponse(dependencyTreeResult);
    }

    private List<File> getServerLibrariesJarFiles(ArtifactManager artifactManager, ArtifactDescriptorResult artifactDescriptor) throws DependencyCollectionException, ArtifactDescriptorException, ArtifactResolutionException {
        List<Dependency> dependencies = artifactManager.getDependencies(artifactDescriptor);
        List<ArtifactDescriptorResult> serverLibrariesDescriptors = artifactManager.getDependenciesDescriptors(dependencies);
        return artifactManager.getArtifactsFiles(serverLibrariesDescriptors);
    }
}
