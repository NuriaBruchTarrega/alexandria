package nl.uva.alexandria.model.sattose;

import java.util.List;

public class Graph {
    private List<InternalCall> internalCalls;
    private List<ExternalCall> externallCalls;

    public Graph(List<InternalCall> internalCalls, List<ExternalCall> externallCalls) {
        this.internalCalls = internalCalls;
        this.externallCalls = externallCalls;
    }

    public List<InternalCall> getInternalCalls() {
        return internalCalls;
    }

    public List<ExternalCall> getExternallCalls() {
        return externallCalls;
    }
}
