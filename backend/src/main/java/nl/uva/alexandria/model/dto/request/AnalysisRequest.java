package nl.uva.alexandria.model.dto.request;

public class AnalysisRequest {

    private String groupID;
    private String artifactID;
    private String version;

    public String getGroupID() {
        return groupID;
    }

    public String getArtifactID() {
        return artifactID;
    }

    public String getVersion() {
        return version;
    }
}
