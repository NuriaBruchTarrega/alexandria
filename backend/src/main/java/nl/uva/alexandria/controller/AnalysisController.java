package nl.uva.alexandria.controller;

import nl.uva.alexandria.logic.Analyzer;
import nl.uva.alexandria.model.dto.request.AnalysisRequest;
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
    public void analyze(@RequestBody AnalysisRequest request) {
        analyzer.analyze(request.getClientJarPath(), request.getClientIdentifier());
    }
}
