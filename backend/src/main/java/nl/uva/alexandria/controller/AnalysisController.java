package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.dto.request.AnalysisRequest;
import nl.uva.alexandria.model.dto.request.ComparisonRequest;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

    private final Analyzer analyzer;

    public AnalysisController(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @CrossOrigin
    @PostMapping("/analyze")
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        return analyzer.analyze(request.getGroupID(), request.getArtifactID(), request.getVersion());
    }

    @CrossOrigin
    @PostMapping("/comparison")
    public void comparison(@RequestBody ComparisonRequest request) {
    }
}
