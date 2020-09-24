package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.experiments.AnalysisSummary;

import java.util.HashSet;
import java.util.Set;

public class ValidationResponse {

    private int totalAnalyzedDependencies = 0;
    private int totalCouplingIsNotEnough = 0;
    private int totalMicIsNotEnough = 0;
    private int totalAcIsNotEnough = 0;
    private Set<String> couplingIsNotEnoughLibraries = new HashSet<>();
    private Set<String> micIsNotEnoughLibraries = new HashSet<>();
    private Set<String> acIsNotEnoughLibraries = new HashSet<>();

    private ValidationResponse() {
    }

    public static ValidationResponse from(Set<AnalysisSummary> analysisSummarySet) {
        ValidationResponse newValidationResponse = new ValidationResponse();

        analysisSummarySet.forEach(analysisSummary -> {
            int totalDependencies = analysisSummary.getIsCouplingEnoughMap().size();
            Set<String> couplingIsNotEnoughLibraries = analysisSummary.getCouplingNotEnoughLibraries();
            Set<String> micIsNotEnoughLibraries = analysisSummary.getMicNotEnoughLibraries();
            Set<String> acIsNotEnoughLibraries = analysisSummary.getAcNotEnoughLibraries();

            newValidationResponse.totalAnalyzedDependencies += totalDependencies;
            newValidationResponse.totalMicIsNotEnough += micIsNotEnoughLibraries.size();
            newValidationResponse.totalAcIsNotEnough += acIsNotEnoughLibraries.size();
            newValidationResponse.totalCouplingIsNotEnough += couplingIsNotEnoughLibraries.size();

            newValidationResponse.couplingIsNotEnoughLibraries.addAll(couplingIsNotEnoughLibraries);
            newValidationResponse.micIsNotEnoughLibraries.addAll(micIsNotEnoughLibraries);
            newValidationResponse.acIsNotEnoughLibraries.addAll(acIsNotEnoughLibraries);
        });

        return newValidationResponse;
    }

    public int getTotalAnalyzedDependencies() {
        return totalAnalyzedDependencies;
    }

    public int getTotalCouplingIsNotEnough() {
        return totalCouplingIsNotEnough;
    }

    public int getTotalMicIsNotEnough() {
        return totalMicIsNotEnough;
    }

    public int getTotalAcIsNotEnough() {
        return totalAcIsNotEnough;
    }

    public Set<String> getCouplingIsNotEnoughLibraries() {
        return couplingIsNotEnoughLibraries;
    }

    public Set<String> getMicIsNotEnoughLibraries() {
        return micIsNotEnoughLibraries;
    }

    public Set<String> getAcIsNotEnoughLibraries() {
        return acIsNotEnoughLibraries;
    }

    @Override
    public String toString() {
        return "ValidationResponse{" +
                "totalAnalyzedDependencies=" + totalAnalyzedDependencies +
                ", totalCouplingIsNotEnough=" + totalCouplingIsNotEnough +
                ", totalMicIsNotEnough=" + totalMicIsNotEnough +
                ", totalAcIsNotEnough=" + totalAcIsNotEnough +
                ", couplingIsNotEnoughLibraries=" + couplingIsNotEnoughLibraries +
                ", micIsNotEnoughLibraries=" + micIsNotEnoughLibraries +
                ", acIsNotEnoughLibraries=" + acIsNotEnoughLibraries +
                '}';
    }
}
