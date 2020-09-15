package nl.uva.alexandria.logic.validation;

import nl.uva.alexandria.model.validation.AnalysisSummary;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ValidateExperiment {
    public static Set<AnalysisSummary> run(String pathToFile) {
        return new HashSet<>();
    }
}
