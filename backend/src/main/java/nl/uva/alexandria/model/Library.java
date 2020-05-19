package nl.uva.alexandria.model;

import java.io.File;

public class Library {

    private final String groupID;
    private final String artifactID;
    private final String version;

    public Library(String groupID, String artifactID, String version) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }

    public String getLibraryPath() {
        String basePath = String.join(File.separator, System.getProperty("user.home"), ".m2", "repository");
        String libraryFolderPath = String.join(File.separator, basePath, groupID.replace(".", File.separator), artifactID, version);
        return String.join(File.separator, libraryFolderPath, artifactID + "-" + version + ".jar");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Library) {
            Library library = (Library) obj;
            return this.groupID.equals(library.groupID)
                    && this.artifactID.equals(library.artifactID)
                    && this.version.equals(library.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return groupID.hashCode() + artifactID.hashCode() + version.hashCode();
    }

    @Override
    public String toString() {
        return groupID + ":" + artifactID + ":" + version;
    }
}
