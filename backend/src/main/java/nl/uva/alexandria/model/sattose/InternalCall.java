package nl.uva.alexandria.model.sattose;

public class InternalCall {
    private Integer sourceMethod;
    private Integer targetMethod;

    public InternalCall(Integer sourceMethod, Integer targetMethod) {
        this.sourceMethod = sourceMethod;
        this.targetMethod = targetMethod;
    }

    public Integer getSourceMethod() {
        return sourceMethod;
    }

    public Integer getTargetMethod() {
        return targetMethod;
    }
}
