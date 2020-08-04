package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.logic.comparison.AnalysisComparator;
import nl.uva.alexandria.model.comparison.Difference;
import nl.uva.alexandria.model.dto.request.AnalysisRequest;
import nl.uva.alexandria.model.dto.request.ComparisonRequest;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import nl.uva.alexandria.model.dto.response.ComparisonResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class AnalysisController {

    private final Analyzer analyzer;
    private final AnalysisComparator analysisComparator;

    public AnalysisController(Analyzer analyzer, AnalysisComparator analysisComparator) {
        this.analyzer = analyzer;
        this.analysisComparator = analysisComparator;
    }

    @CrossOrigin
    @PostMapping("/analyze")
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        return analyzer.analyze(request.getGroupID(), request.getArtifactID(), request.getVersion());
    }

    @CrossOrigin
    @PostMapping("/comparison")
    public ComparisonResponse comparison(@RequestBody ComparisonRequest request) {
        Set<Difference> differences = analysisComparator.compare(request.getPathToFile());
        return new ComparisonResponse(differences);
    }
}
