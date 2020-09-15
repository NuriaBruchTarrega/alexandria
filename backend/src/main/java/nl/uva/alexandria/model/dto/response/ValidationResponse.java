package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.validation.AnalysisSummary;

import java.util.Set;

public class ValidationResponse {
    public static ValidationResponse from(Set<AnalysisSummary> analysisSummarySet) {
        return new ValidationResponse();
    }
}
