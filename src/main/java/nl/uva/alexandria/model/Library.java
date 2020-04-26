package nl.uva.alexandria.model;

public class Library {

    private final String group;
    private final String artifact;
    private final Version version;

    public Library(String group, String artifact, Version version) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getArtifact() {
        return artifact;
    }

    public Version getVersion() {
        return version;
    }
}
