package nl.uva.alexandria.model.dto.response;

import nl.uva.alexandria.model.experiments.Difference;

import java.util.Set;

public class ComparisonResponse {
    private Set<Difference> differences;

    public ComparisonResponse(Set<Difference> differences) {
        this.differences = differences;
    }

    public Set<Difference> getDifferences() {
        return differences;
    }
}
