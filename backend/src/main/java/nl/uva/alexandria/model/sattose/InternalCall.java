package nl.uva.alexandria.model.sattose;

public class InternalCall {
    private Long sourceMethod;
    private Long targetMethod;

    public InternalCall(Long sourceMethod, Long targetMethod) {
        this.sourceMethod = sourceMethod;
        this.targetMethod = targetMethod;
    }

    public Long getSourceMethod() {
        return sourceMethod;
    }

    public Long getTargetMethod() {
        return targetMethod;
    }
}
