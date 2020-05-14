package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.dto.request.AnalysisRequest;
import nl.uva.alexandria.model.dto.response.AnalysisResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

    private final Analyzer analyzer;

    public AnalysisController(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @PostMapping("/analyze")
    public AnalysisResponse analyze(@RequestBody AnalysisRequest request) {
        return analyzer.analyze(request.getClientJarPath(), request.getClientIdentifier());
    }
}
