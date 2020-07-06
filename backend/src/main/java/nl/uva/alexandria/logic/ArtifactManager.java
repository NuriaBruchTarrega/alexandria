package nl.uva.alexandria.logic;

import nl.uva.alexandria.model.DependencyTreeNode;
import nl.uva.alexandria.model.Library;
import nl.uva.alexandria.model.factories.LibraryFactory;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.*;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static nl.uva.alexandria.logic.LocalRepo.localRepoBasePath;
import static nl.uva.alexandria.logic.LocalRepo.localRepoFile;

class ArtifactManager {

    private final List<RemoteRepository> remotes;
    private final RepositorySystem repositorySystem;
    private final DefaultRepositorySystemSession defaultRepositorySystemSession;

    private List<String> directDependencies = new ArrayList<>();
    private List<String> allDependencies = new ArrayList<>();
    private DependencyNode dependencyRootNode;

    ArtifactManager() {
        this.repositorySystem = newRepositorySystem();
        this.defaultRepositorySystemSession = MavenRepositorySystemUtils.newSession();

        final LocalRepository local = new LocalRepository(localRepoFile);
        defaultRepositorySystemSession.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(defaultRepositorySystemSession, local));
        defaultRepositorySystemSession.setDependencySelector(new MyDependencySelector());

        this.remotes = Arrays.asList(
                new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build()
        );
    }

    List<String> getDirectDependencies() {
        return directDependencies;
    }

    List<String> getAllDependencies() {
        return allDependencies;
    }

    ArtifactDescriptorResult getArtifactDescriptor(String groupID, String artifactID, String version) throws ArtifactDescriptorException, ArtifactResolutionException {
        DefaultArtifact artifact = new DefaultArtifact(groupID, artifactID, "jar", version);
        ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, remotes, null);
        ArtifactDescriptorResult result = repositorySystem.readArtifactDescriptor(defaultRepositorySystemSession, request);

        downloadArtifactJar(artifact);

        return result;
    }

    File getArtifactFile(ArtifactDescriptorResult artifactDescriptorResult) {
        Artifact artifact = artifactDescriptorResult.getArtifact();
        String pathToGroupFolder = artifact.getGroupId().replace(".", File.separator);
        String jarFileName = artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar";
        return new File(String.join(File.separator, localRepoBasePath, pathToGroupFolder, artifact.getArtifactId(), artifact.getVersion(), jarFileName));
    }

    List<File> getArtifactsFiles(List<ArtifactDescriptorResult> artifactDescriptorResults) {
        return artifactDescriptorResults.stream().map(this::getArtifactFile).collect(Collectors.toList());
    }

    List<Dependency> getDependencies(ArtifactDescriptorResult artifactDescriptorResult) throws DependencyCollectionException {
        CollectRequest request = new CollectRequest(new Dependency(artifactDescriptorResult.getArtifact(), null), remotes);

        CollectResult result = repositorySystem.collectDependencies(defaultRepositorySystemSession, request);
        this.dependencyRootNode = result.getRoot();
        List<Dependency> dependencies = getDependenciesFromTree(this.dependencyRootNode);

        saveDirectDependencies(this.dependencyRootNode.getChildren());
        saveAllDependencies(dependencies);

        return dependencies;
    }

    List<ArtifactDescriptorResult> getDependenciesDescriptors(List<Dependency> dependencies) throws ArtifactDescriptorException, ArtifactResolutionException {
        List<ArtifactDescriptorResult> artifactDescriptorResults = new ArrayList<>();

        for (Dependency dependency : dependencies) {
            Artifact artifact = dependency.getArtifact();
            if (artifact.getExtension().equals("jar")) { // TODO: what to do with zips?
                artifactDescriptorResults.add(getArtifactDescriptor(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion()));
            }
        }

        return artifactDescriptorResults;
    }

    public DependencyTreeNode generateCustomDependencyTree() {
        return createDependencyTreeNode(this.dependencyRootNode);
    }

    private DependencyTreeNode createDependencyTreeNode(DependencyNode dependencyRootNode) {
        String gav = getGAVFromArtifact(dependencyRootNode.getDependency().getArtifact());
        Library library = LibraryFactory.getLibraryFromGAV(gav);
        DependencyTreeNode node = new DependencyTreeNode(library);

        for (DependencyNode dependency : dependencyRootNode.getChildren()) {
            node.addChild(createDependencyTreeNode(dependency));
        }

        return node;
    }

    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    private ArtifactResult downloadArtifactJar(Artifact artifact) throws ArtifactResolutionException {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        remotes.forEach(artifactRequest::addRepository);

        // To download jar file of the artifact to the local repo
        return repositorySystem.resolveArtifact(defaultRepositorySystemSession, artifactRequest);
    }

    private List<Dependency> getDependenciesFromTree(DependencyNode root) {
        List<Dependency> dependencies = new ArrayList<>();
        Queue<DependencyNode> toVisit = new ArrayDeque<>(root.getChildren());

        while (!toVisit.isEmpty()) {
            DependencyNode visiting = toVisit.poll();
            toVisit.addAll(visiting.getChildren());
            dependencies.add(visiting.getDependency());
        }

        return dependencies;
    }

    private void saveDirectDependencies(List<DependencyNode> directDependencies) {
        this.directDependencies = directDependencies.stream()
                .map(directDependency -> getGAVFromArtifact(directDependency.getDependency().getArtifact()))
                .collect(Collectors.toList());
    }

    private void saveAllDependencies(List<Dependency> dependencies) {
        this.allDependencies = dependencies.stream()
                .map(dependency -> getGAVFromArtifact(dependency.getArtifact()))
                .collect(Collectors.toList());
    }

    private String getGAVFromArtifact(Artifact artifact) {
        return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
    }

    private static class MyDependencySelector implements DependencySelector {

        @Override
        public boolean selectDependency(Dependency dependency) {
            String scope = dependency.getScope();
            return scope.equals("compile") || scope.equals("provided");
        }

        @Override
        public DependencySelector deriveChildSelector(DependencyCollectionContext context) {
            return this;
        }
    }
}
