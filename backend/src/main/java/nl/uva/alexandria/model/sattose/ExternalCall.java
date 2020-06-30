package nl.uva.alexandria.model.sattose;

import java.util.Map;

public class ExternalCall {
    private Long sourceMethod;
    private String targetMethod;
    private Map<String, String> metadata;

    public ExternalCall(Long sourceMethod, String targetMethod, Map<String, String> metadata) {
        this.sourceMethod = sourceMethod;
        this.targetMethod = targetMethod;
        this.metadata = metadata;
    }

    public Long getSourceMethod() {
        return sourceMethod;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
