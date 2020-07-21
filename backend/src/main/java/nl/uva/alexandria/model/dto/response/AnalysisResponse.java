package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.result.DependencyTreeResult;

public class AnalysisResponse {
    private DependencyTreeResult dependencyTreeResult;

    public AnalysisResponse(DependencyTreeResult dependencyTreeResult) {
        this.dependencyTreeResult = dependencyTreeResult;
    }

    public DependencyTreeResult getDependencyTreeResult() {
        return dependencyTreeResult;
    }

    public void setDependencyTreeResult(DependencyTreeResult dependencyTreeResult) {
        this.dependencyTreeResult = dependencyTreeResult;
    }
}
