package nl.uva.alexandria.model;

public class Library {

    private final String path;
    private final String groupID;
    private final String artifactID;
    private final String version;

    public Library(String path, String groupID, String artifactID, String version) {
        this.path = path;
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
    }

    public String getPath() {
        return path;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Library) {
            Library library = (Library) obj;
            return this.path.equals(library.path)
                    && this.groupID.equals(library.groupID)
                    && this.artifactID.equals(library.artifactID)
                    && this.version.equals(library.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return path.hashCode() + groupID.hashCode() + artifactID.hashCode() + version.hashCode();
    }
}
