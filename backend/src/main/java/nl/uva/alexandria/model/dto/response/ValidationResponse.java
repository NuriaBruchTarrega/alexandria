package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.validation.AnalysisSummary;

import java.util.HashSet;
import java.util.Set;

public class ValidationResponse {

    private int totalAnalyzedDependencies = 0;
    private int totalCouplingIsNotEnough = 0;
    private Set<String> couplingIsNotEnoughLibraries = new HashSet<>();

    private ValidationResponse() {
    }

    public static ValidationResponse from(Set<AnalysisSummary> analysisSummarySet) {
        ValidationResponse newValidationResponse = new ValidationResponse();

        analysisSummarySet.forEach(analysisSummary -> {
            int totalDependencies = analysisSummary.getIsCouplingEnoughMap().size();
            Set<String> couplingIsNotEnoughLibraries = analysisSummary.getCouplingNotEnoughLibraries();
            int numCouplingIsNotEnough = couplingIsNotEnoughLibraries.size();

            newValidationResponse.addAnalyzedDependencies(totalDependencies);
            newValidationResponse.addCouplingIsNotEnough(numCouplingIsNotEnough);
            newValidationResponse.addCouplingIsNotEnoughLibraries(couplingIsNotEnoughLibraries);
        });

        return newValidationResponse;
    }

    public int getTotalAnalyzedDependencies() {
        return totalAnalyzedDependencies;
    }

    public int getTotalCouplingIsNotEnough() {
        return totalCouplingIsNotEnough;
    }

    public Set<String> getCouplingIsNotEnoughLibraries() {
        return couplingIsNotEnoughLibraries;
    }

    private void addAnalyzedDependencies(int analyzedDependencies) {
        this.totalAnalyzedDependencies += analyzedDependencies;
    }

    private void addCouplingIsNotEnough(int numCouplingIsNotEnough) {
        this.totalCouplingIsNotEnough += numCouplingIsNotEnough;
    }

    private void addCouplingIsNotEnoughLibraries(Set<String> couplingIsNotEnoughLibraries) {
        this.couplingIsNotEnoughLibraries.addAll(couplingIsNotEnoughLibraries);
    }

    @Override
    public String toString() {
        return "ValidationResponse{" +
                "totalAnalyzedDependencies=" + totalAnalyzedDependencies +
                ", totalCouplingIsNotEnough=" + totalCouplingIsNotEnough +
                ", couplingIsNotEnoughLibraries=" + couplingIsNotEnoughLibraries +
                '}';
    }
}
