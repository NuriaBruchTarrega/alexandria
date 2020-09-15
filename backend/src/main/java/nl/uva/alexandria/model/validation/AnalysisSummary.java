package nl.uva.alexandria.model.validation;

import nl.uva.alexandria.model.DependencyTreeResult;

public class AnalysisSummary {
    public static AnalysisSummary from(DependencyTreeResult result) {
        return new AnalysisSummary();
    }
}
