package nl.uva.alexandria.model.sattose;

import java.util.Map;

public class ExternalCall {
    private Integer sourceMethod;
    private String targetMethod;
    private Map<String, String> metadata;

    public ExternalCall(Integer sourceMethod, String targetMethod, Map<String, String> metadata) {
        this.sourceMethod = sourceMethod;
        this.targetMethod = targetMethod;
        this.metadata = metadata;
    }

    public Integer getSourceMethod() {
        return sourceMethod;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
