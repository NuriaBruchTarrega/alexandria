package nl.uva.alexandria.logic;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ArtifactManager {

    public static void main(String[] args) {
        try {
            ArtifactDescriptorResult ar = getArtifactDescriptor("com.puppycrawl.tools", "checkstyle", "8.32");
            File jar = getArtifactFile(ar);
            String helo = "helo";
        } catch (ArtifactDescriptorException | ArtifactResolutionException e) {
            e.printStackTrace();
        }
    }

    public static ArtifactDescriptorResult getArtifactDescriptor(String groupID, String artifactID, String version) throws ArtifactDescriptorException, ArtifactResolutionException {
        File localRepo = new File(String.join(File.separator, System.getProperty("user.home"), ".m2", "repository"));

        RepositorySystem repositorySystem = newRepositorySystem();
        DefaultRepositorySystemSession defaultRepositorySystemSession = MavenRepositorySystemUtils.newSession();

        final LocalRepository local = new LocalRepository(localRepo);
        defaultRepositorySystemSession.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(defaultRepositorySystemSession, local));

        List<RemoteRepository> remotes = Arrays.asList(
                new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build()
        );

        DefaultArtifact artifact = new DefaultArtifact(groupID, artifactID, "jar", version);
        ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, remotes, null);
        ArtifactDescriptorResult result = repositorySystem.readArtifactDescriptor(defaultRepositorySystemSession, request);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.addRepository(remotes.get(0));

        ArtifactResult artifactResult = repositorySystem.resolveArtifact(defaultRepositorySystemSession, artifactRequest);

        return result;
    }

    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    public static File getArtifactFile(ArtifactDescriptorResult artifactDescriptorResult) {
        Artifact artifact = artifactDescriptorResult.getArtifact();
        String pathToJarFolder = artifact.getGroupId().replace(".", File.separator) + File.separator + artifact.getArtifactId();
        String jarFileName = artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar";
        File jarFile = new File(String.join(File.separator, System.getProperty("user.home"), ".m2", "repository", pathToJarFolder, jarFileName));
        return jarFile;
    }
}
